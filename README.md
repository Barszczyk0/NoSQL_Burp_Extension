## NoSQL NeedleN inja

Burp Suite extension for detection and exploitation of variants of the NoSQL Injection vulnerability. Made for the free version of Burp Suite. Uses Montoya API. 

This is a proof of concept solution. Tested only with labs from PortSwigger Web Security Academy.

## Installation
Compile the project to a JAR file.

Follow the official Burp Suite instructions for installing extensions from https://portswigger.net/burp/documentation/desktop/extensions/installing-extensions#installing-an-extension-from-a-file

## Usage

## Test: FUZZ_STRING

To perform the **FUZZ_STRING** test:

1. Select the part of the request that should be replaced with the payload.
2. In the context menu (available in "Target", "Proxy", "Repeater", or "Logger"), select **"Perform test: FUZZ_STRING"**.
3. The scan will automatically start, and sent requests can be observed in the "NNN Logger".
4. The test first sends the full payload and then sends each character separately to identify which specific characters might cause server errors.
5. This test works with both GET and POST requests.

![](/attachments/fuzz.png)

After the scan, all responses will be analyzed, and suspicious ones will be listed in the **"NNN Scan Information"** tab within the "NNN Logger".

![](/attachments/fuzzingresult.png)

## Test: BOOLEAN

To perform the **BOOLEAN** test:

1. Select the part of the request to be replaced with the payload.
2. In the context menu, choose **"Perform test: BOOLEAN"**.
3. The scan starts automatically, and requests can be tracked in the "NNN Logger".
4. This test sends payloads with logic operators.
5. It works with both GET and POST requests.

![](/attachments/bool.png)

After the scan, suspicious responses will be listed in the **"NNN Scan Information"** tab.

![](/attachments/booleanresult.png)

## Test: Authentication Bypass

### AUTHENTICATION_BYPASS_USERNAME

To perform the **AUTHENTICATION_BYPASS_USERNAME** test:

1. Select the **username field** (including quotes) in the request.
2. In the context menu, choose **"Perform test: AUTHENTICATION_BYPASS_USERNAME"**.
3. The scan starts automatically, and requests can be observed in the "NNN Logger".
4. The test verifies parameters storing usernames.
5. This test works with both GET and POST requests.

![](/attachments/usernamedemo.png)

After the scan, suspicious responses will be listed in **"NNN Scan Information"**.

![](/attachments/usernameresult.png)

### AUTHENTICATION_BYPASS_PASSWORD

To perform the **AUTHENTICATION_BYPASS_PASSWORD** test:

1. Select the **password field** (including quotes) in the request.
2. In the context menu, choose **"Perform test: AUTHENTICATION_BYPASS_PASSWORD"**.
3. The scan starts automatically, and requests can be observed in the "NNN Logger".
4. The test verifies parameters storing passwords.

![](/attachments/passworddemo.png)

After the scan, suspicious responses will be listed in **"NNN Scan Information"**.

![](/attachments/passwordresult.png)

### AUTHENTICATION_TEST

To perform the **AUTHENTICATION_TEST** for both username and password:

1. Select the **username field** and use **"[AUTHENTICATION TEST] Select username field"**.
2. Select the **password field** and use **"[AUTHENTICATION TEST] Select password field"**.
3. Once both are selected, the scan starts automatically.
4. This test attempts to bypass authentication using payloads from previous tests.
5. It works with POST requests.

![](/attachments/aubypass1.png) ![](/attachments/aubypass2.png)

After the scan, suspicious responses will be listed in **"NNN Scan Information"**.

![](/attachments/aubypassresult.png)

## Extraction of Field Names

To perform **FIELD_NAME_EXTRACTION**:

1. Select the part of the request where the payload should be added.
2. In the context menu, choose **"Perform extraction of field names"**.
3. The scan starts automatically, and requests can be observed in the "NNN Logger".
4. The test attempts to extract field names from responses.
5. It works with POST requests.

![](/attachments/fieldnameextraction.png)

After the scan, extracted field names will be listed in **"NNN Scan Information"**.

![](/attachments/fieldnameextractiondemo.png)

## Data Extraction

To perform **DATA_EXTRACTION**:

1. Select the part of the request where the payload should be added.
2. In the context menu, choose **"Perform extraction of data"**.
3. The scan starts automatically, and requests can be observed in the "NNN Logger".
4. This test attempts to extract data based on known field names.
5. It works with GET and POST requests.

![](/attachments/dataextractiondemo.png)

After the scan, extracted data will be listed in **"NNN Scan Information"**.

![](/attachments/dataextractionresult.png)

### Data Extraction via POST

![](/attachments/dataextractionpost1.png) ![](/attachments/dataextractiondemo2.png)

