The Devoxx Sherlock Watson project
==================================

This Devoxx Sherlock IBM Watson project allows you to upload audio files and articles which are processed by different Watson services like Concept Insights, Speech to Text and Alchemy Language. 

## Running the Application Locally in Eclipse with Liberty

1. Download and install [IBM Eclipse Tools for Bluemix](https://developer.ibm.com/wasdev/downloads/#asset/tools-IBM_Eclipse_Tools_for_Bluemix).
2. In the Servers view of Eclipse, right-click to create a new WAS Liberty server. Follow the steps in the wizard, which includes the option to download and install the WAS Liberty runtime.
3. Import the project into Eclipse using *File -> Import -> Maven -> Existing Maven Projects* option.
4. TODO Deploy 
5. TODO Go to

## Running the Application in Bluemix using Eclipse

1. Download and install [IBM Eclipse Tools for Bluemix](https://developer.ibm.com/wasdev/downloads/#asset/tools-IBM_Eclipse_Tools_for_Bluemix).
2. In the Servers view of Eclipse, right-click to create a new IBM Bluemix server. Follow the steps in the wizard.
3. Import this sample into Eclipse using *File -> Import -> Maven -> Existing Maven Projects* option.
4. Deploy the project into Bluemix server. 


## Watson service api-keys

You need to create an  __application.properties__ file in  __/src/main/resources/__ with your Watson credentials & api-keys

~~~~ 
# Speech to text credentials
speech.username=<ADD YOUR CREDENTIALS FOR SPEECH>
speech.password=<ADD YOUR CREDENTIALS FOR SPEECH>

# ConceptInsights credentials
insight.username=<ADD YOUR CREDENTIALS FOR CONCEPT INSIGHTS>
insight.password=<ADD YOUR CREDENTIALS FOR CONCEPT INSIGHTS>

# Translation credentials
translate.username=<ADD YOUR CREDENTIALS FOR TRANSLATE>
translate.password=<ADD YOUR CREDENTIALS FOR TRANSLATE>

# Please don't change this 'sandbox' corpus name
corpus.name=devoxx_sandbox_corpus

# Alchemy credentials
alchemy.apikey=<ADD YOUR API-KEY FOR ALCHEMY>
~~~~ 

## Running with Maven

This project can be build with [Apache Maven](http://maven.apache.org/). The project uses [Liberty Maven Plug-in][] to automatically download and install Liberty profile runtime from the [Liberty repository](https://developer.ibm.com/wasdev/downloads/). Liberty Maven Plug-in is also used to create, configure, and run the application on the Liberty server. 

Use the following steps to run the application with Maven:

1. Execute full Maven build. This will cause Liberty Maven Plug-in to download and install Liberty profile server.
    ```bash
    $ mvn clean install
    ```

2. To run a local Liberty server with the JavaHelloWorldApp sample execute:
    ```bash
    $ mvn liberty:run-server
    ```

Once the server is running, the application will be available under [http://localhost:9080/SherlockWatsonUploaderProcessor](http://localhost:9080/SherlockWatsonUploaderProcessor).


3. To push the application to Bluemix using the cf command line tool:
    ```bash
    $ cf push <appname> -p target/DevoxxWatsonAudioFileProcessor.war
    ```

## Test REST interface using Swagger

1. Surf to http://sherlock.devoxx.com/swagger/
2. Enter http://sherlock.devoxx.com/v2/api-docs 
3. You can now explore the Search REST method

## Related YouTube video
  
[![YouTube Video](http://img.youtube.com/vi/KC7CMrLLm14/0.jpg)](https://www.youtube.com/watch?v=KC7CMrLLm14)

Watch [Stephan Janssen](https://twitter.com/stephan007) and [Sandhya Kapoor](https://twitter.com/sandhyakapoor9) explain the Devoxx Sherlock project during DevoxxUK

# License

```text
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
````
