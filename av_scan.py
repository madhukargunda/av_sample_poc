
import asyncio
import aiohttp
import warnings
import base64
import random
import string
import requests
import httpx
import logging

from urllib3.exceptions import InsecureRequestWarning
warnings.simplefilter('ignore', InsecureRequestWarning)
from requests_toolbelt.multipart.encoder import MultipartEncoder

print("Antivirus scaning initiated.................")

# Login into MATD System and retrieve the 
def login_to_matd(url):

    headers= {
        "Accept": "application/vnd.ve.v1.0+json",
        "Content-Type": "application/json",
        "VE-SDK-API": "1234567890",
        "VE-API-VERSION": ""
    }
    response = requests.get(url,headers=headers)
    if response.status_code == 200:
        data = response.json()
        # Access and process the synchronous response data
        results = data['results']
        # Properties to encode and combine
        property1 = "session"
        property2 = "userId"

        # Encode the combination of properties
        if property1 in results and property2 in results:
            encoded_combination = base64.b64encode((str(results[property1]) + ":" + str(results[property2])).encode("utf-8")).decode("utf-8")
            #Converting the userId and 
            print("userId API Response:", encoded_combination)
            return encoded_combination
    else:
        print("Error: ", response.status_code)



#File/URL Submission 
def file_or_url_submission(url,base64_encoded_string):

    data = {
        'data': '{"data":{"xMode": 0,"overrideOS": 1,"messageId": "","vmProfileList": "11","submitType": "0","url": ""},"filePriorityQ":"run_now"}'
    }
    files = {
        'amas_filename': ('/Users/madhu/Documents/ePaySlip_EP3538_2022-08_M_46_NP.pdf', open('/Users/madhu/Documents/ePaySlip_EP3538_2022-08_M_46_NP.pdf', 'rb'))
    }

    # Create a multipart encoder with the file
    multipart_encoder = MultipartEncoder(fields={'file': ('/Users/madhu/Documents/ePaySlip_EP3538_2022-08_M_46_NP.pdf', open('/Users/madhu/Documents/ePaySlip_EP3538_2022-08_M_46_NP.pdf', 'rb'), 'application/octet-stream')})

    headers= {
        "Accept": "application/vnd.ve.v1.0+json",
       # "Content-Type": multipart_encoder.content_type,
        "VE-SDK-API": base64_encoded_string,
        "VE-API-VERSION": ""
    }

    response = requests.post(url, headers=headers, files=files, data=data)
    print(response.text)
    
    if response.status_code == 200:
        data = response.json()
        results = data['results']
        taskid = results[0]['taskId']  
        print("Taskid:",taskid)
    else:
        print("Error in else :", response.status_code)


# Asynchronous API call using httpx
async def async_api_call(url, base64_encoded_string, target_value):
    try:
        await asyncio.sleep(2)
        headers= {
            "Accept": "application/vnd.ve.v1.0+json",
            "Content-Type": "application/json",
            "VE-SDK-API": base64_encoded_string,
            "VE-API-VERSION": ""
        }
        async with httpx.AsyncClient() as client:
            #while True:
                response = await client.get(url , headers=headers)
                print(response)
                if response.status_code == 200 and response.json() == target_value:
                    return response.json()
                await asyncio.sleep(1)  # Add a delay before making the next API call
    except httpx.HTTPError as e:
        logging.error(f"HTTP error occurred during API call: {e}")
    except httpx.RequestError as e:
        logging.error(f"Request error occurred during API call: {e}")
    except httpx.TimeoutException as e:
        logging.error(f"Timeout occurred during API call: {e}")
    except Exception as e:
        logging.error(f"An unexpected error occurred during API call: {e}")

async def long_polling_api(url,base64_encoded_string, target_value):
    headers= {
        "Accept": "application/vnd.ve.v1.0+json",
        "Content-Type": "application/json",
        "VE-SDK-API": base64_encoded_string,
        "VE-API-VERSION": ""
    }
    while True:
        try:
            async with httpx.AsyncClient() as client:
                response = await client.get(url,headers=headers)
                response.raise_for_status()
                data = response.json()

                # Process the data received from the API
                if "Summary" in data:
                    print("Received updated data:", data["Summary"])

                # Continue long polling with a delay of 5 seconds
                await asyncio.sleep(5)
        except httpx.HTTPError as e:
            print("HTTP Error occurred:", e)
        except httpx.RequestError as e:
            print("Request Error occurred:", e)
        except Exception as e:
            print("An unexpected error occurred:", e)


async def main():

    # URLs of the three APIs

    try:
        #login to MATD , Make the first API request and get the response data
        base64_encoded_string =  login_to_matd("http://localhost:8080/login")
        file_or_url_submission("http://localhost:8080/upload",base64_encoded_string)

        #Async Headers Prepared
        Async_headers= {
            "Accept": "application/vnd.ve.v1.0+json",
            "VE-SDK-API": base64_encoded_string,
            "VE-API-VERSION": ""
        }
            # Wait for the asynchronous API call to complete
        #response_3 = await async_api_call("http://localhost:8080/showreport", base64_encoded_string, "Summary")
        await long_polling_api("http://localhost:8080/showreport", base64_encoded_string, "Summary")
        # Check the response of the asynchronous API call
        # if response_3.status_code == 200 and "Summary" in response_3.json():
          #  print("All API calls successfull")
         #else:
           # print("The last API call did not return the expected response.")

    except httpx.HTTPError as e:
        logging.error("HTTP error occurred during API call: {e}")
    except httpx.RequestError as e:
        logging.error("Request error occurred during API call: {e}")
    except httpx.TimeoutException as e:
        logging.error(f"Timeout occurred during API call: {e}")
    except Exception as e:
        logging.error("An unexpected error occurred during API call: {e}")

# Run the asynchronous event loop to execute the main function
if __name__ == "__main__":
    asyncio.run(main())