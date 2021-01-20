'''
Created on Jun 26, 2020

@author: Richard.Wang
'''
import requests
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
    
def getProdDetails(accessToken, gtin, gln):   
    restSrvUrl = 'https://demo-api-contentdistribution.gs1ca.org/api/products/'

    # Dictionary of query parameters (if any)
    restSrvUrl = restSrvUrl + gtin
    payload = {
        'ims' : 'ecommerceContent',
#        'ims' : 'planoContent',
#      'ims' : 'marketingContent',
        'gln' : gln,
    }
    
    headers = {'Authorization': 'Bearer ' + accessToken}

    result = requests.get(restSrvUrl, params=payload, headers=headers)
    print(result.status_code)
    return result
 
# Get AccessTOken
at=getAccessToken()

# Specify gtin and baseGln
# gtin = '12919000088'
# baseGln = '0672626000018'

#gtin = '054467050436'
#baseGln = '0672626000018'
#prodDetails = getProdDetails(at, gtin, baseGln)

gtin = '060988212031'
gln = '0006098800010'
prodDetails = getProdDetails(at, gtin, gln)


# Convert to Json format
parsed = json.loads(prodDetails.content)

#Print to screen with pretty
print (json.dumps(parsed, indent=2, sort_keys=True))

#Save to file without pretty
#with open('newAndUpdatedProducts.json', 'w') as outfile:
#    json.dump(nuProductsKeyList.json(), outfile)

#Save to file with pretty
#str_json = json.loads(nuProductsKeyList.content)

#with open('newAndUpdatedProducts-pretty.json', 'w') as outfile:
#    print (json.dumps(str_json, indent=2, sort_keys=True), file=outfile)

