# How to use the web service(part 1):

So to get the application to work you need to be in the project directory and run this command
int the terminal : mvn clean spring-boot:run.
After having ran this command you can then access the application on the localhostport 8080.
To get a respone you need to add this url to the end of the http://localhost:8080 : /population?year=YYYY

Examples of working urls would be :
http://localhost:8080/population?year=2015
http://localhost:8080/population?year=1871


# How to build and run the app with docker(part2):

To build the app with docker you need to be again in the project directory and use this command:
docker build -t population-api:1.0 .

Afterward if you want you can check if image build correctly with this command, but thats optional:
docker images population-api

Later you run the conatainer with this command:
docker run --rm -p 8080:8080 population-api:1.0

and finally you should be able to access it now using a valid url given in the examples section.


# Video link for the part(1,2 and 3)

part1:https://youtu.be/bZ6f5ii9vIE
part2:https://youtu.be/Qdz2akZ8Rdg
part3:https://youtu.be/N-REztO3WH4
