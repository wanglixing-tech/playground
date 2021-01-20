'''
Created on Aug 18, 2020

@author: Richard.Wang
'''
from gtin import GTIN
import glob
import os
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

def getProdDetails(accessToken, gtin, gln, targetUPC):   
    restSrvUrl = 'https://demo-api-contentdistribution.gs1ca.org/api/products/'
    detailInfofolder = "C:\\Temp\\test5000\\details-17\\"

    # Dictionary of query parameters (if any)
    restSrvUrl = restSrvUrl + gtin
    payload = {
        'ims' : 'ecommerceContent',
#        'ims' : 'planoContent',
#      'ims' : 'marketingContent',
        'gln' : gln,
    }
    
    headers = {'Authorization': 'Bearer ' + accessToken}

    detailInfo = requests.get(restSrvUrl, params=payload, headers=headers)
    str_json = json.loads(detailInfo.content)            

    with open(detailInfofolder + str(targetUPC) + ".json" , 'w') as outfile:
         print (json.dumps(str_json, indent=2, sort_keys=True), file=outfile)
    
    print(detailInfo.status_code)
    return

# Main procedure
targetfolder = "C:\\Temp\\test5000\\"
activeUPCListFile = "C:\\Users\\Richard.Wang\\Documents\\My_DailyWork\\GS1-REST-API\\ActiveUPCList.txt"

#Get all active UPC codes into a list
with open(activeUPCListFile) as file_in:
    activeUPCList = []
    for line in file_in:
        activeUPCList.append(line.rstrip())

# List all files in a folder    
targetFiles = []
for file in glob.glob(targetfolder + "productKeys-17.json"):
    targetFiles.append(file)
    
        
for targetUPC in activeUPCList:
    targetGTIN = str(GTIN(raw=int(targetUPC)))

    for oneFile in targetFiles:
        # print(oneFile)
        with open(oneFile, 'r') as f:
            key_dict = json.load(f)
            
        allKeys = key_dict["keys"]
        exist = False
        for key in allKeys:
            #print("-targetGTIN-GTIN14=" + targetGTIN + '-'  + key['gtin14'])
            if key['gtin14'] == targetGTIN:
                exist = True
                print("Target UPC-GTIN=" + targetUPC + '-'  + key['gtin14'])
                print(key['baseGln'])
                print(key['gln'])
                
                at=getAccessToken()
                if key['gln'] != None:
                    getProdDetails(at, key['gtin14'], key['gln'], targetUPC)
                else:
                    getProdDetails(at, key['gtin14'], key['baseGln'], targetUPC)                   
                break
        
        if exist:
            print ("Found from file " + oneFile)
            break
        else:
            continue
            
        