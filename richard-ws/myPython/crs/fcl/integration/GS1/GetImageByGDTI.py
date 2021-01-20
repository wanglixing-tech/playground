'''
Created on Jun 26, 2020

@author: Richard.Wang
'''
import requests
import shutil
import json
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
    
def getProdImage(accessToken, gdti, imageFilePath, imageFileName, imageFileType):   
    restSrvUrl = 'https://demo-api-contentdistribution.gs1ca.org/api/images/'

    # Dictionary of query parameters (if any)
    restSrvUrl = restSrvUrl + gdti
    headers = {'Authorization': 'Bearer ' + accessToken}
    imageFileFullName = imageFilePath + '\\' + imageFileName + '.' + imageFileType
    response = requests.get(restSrvUrl, stream=True, headers=headers)
    returnCode = response.status_code
    with open(imageFileFullName, 'wb') as out_file:
        shutil.copyfileobj(response.raw, out_file)
    del response
    return returnCode

# Main process
# Get Access Token
at=getAccessToken()

# Specify which product's image you want to download 
# gtin = '12919000088'
# gtin = '012919000064'
gtin = '054467050436'
baseGln = '0672626000018'

# Imanage format, which can be found from the product details together with GDTI
image_format = 'TIF' 

# Where and what image file name you want to use, here use gdti as imange file name
image_path = 'C:\Temp'
gdti = '754000000014100000000061015977'

prodImageRC = getProdImage(at, gdti, image_path, gdti, image_format)

print(prodImageRC)
