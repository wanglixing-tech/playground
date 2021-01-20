'''
Created on Jun 26, 2020

@author: Richard.Wang
'''
import requests
import json
from basicauth import encode

url = 'https://demo-api-eccnetservices.gs1ca.org/oauth/token'
clientId='fclcduat'
clientSecret='99759D7A-5B53-4465-9CB4-5FDFD02675E7'

# Dictionary of headers (Only Authentication here)
encoded_str = encode(clientId, clientSecret)
headers = {
    'Authorization': encoded_str
}

# Dictionary of query parameters (if any)
payload = {
    'grant_type' : 'password',
    'scope' : 'basic',
    'username' : 'singleapi.test@fcl.ca',
    'password' : '9f2544kjG97YyGnz',
}

response = requests.post(url, data=payload, headers=headers)
body = json.loads(response.content)
token = body["access_token"]
print( token )
