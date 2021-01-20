'''
Created on Jun 26, 2020

@author: Richard.Wang
'''
import requests
import json
import glob
import os
from basicauth import encode

def getAccessToken():
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
    return token
    
def getNewAndUpdatedProductsKeyList(accessToken, saveTo):   
    resSrvUrl = 'https://demo-api-contentdistribution.gs1ca.org/api/products/keys.json'   
    headers = {'Authorization': 'Bearer ' + accessToken}
    offset = 0 

    while True:
        payload = {
            'resultType' : 'brand-new+updated',
            'ims' : 'ecommerceContent',
            'limits' : 5000,
            'cursor' : offset,
        }
        result = requests.get(resSrvUrl, params=payload, headers=headers)
        print(result.status_code)
        #Save to file with pretty
        str_json = json.loads(result.content)            

        with open(saveTo + "productKeys-" + str(offset) + ".json" , 'w') as outfile:
            print (json.dumps(str_json, indent=2, sort_keys=True), file=outfile)
    
        # Get nextCursor
        myNextCursor = str_json["nextCursor"]
        if myNextCursor == '0':
            print ("myNextCursor=0")
            break

        else: 
            offset = offset + 1
            
    return 0


 
# ========================= Main Procedure ============================================#
at=getAccessToken()
workingDir = "C:\\Temp\\test5000\\"

# Create folder if not exist
if not os.path.exists(workingDir):
    os.makedirs(workingDir)

getNewAndUpdatedProductsKeyList(at, workingDir)
#
