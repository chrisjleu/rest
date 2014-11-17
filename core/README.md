# Core services

A Spring (Maven) module that offers services for all the core functionality of the application. 
The core module should be highly unit testable, implying that - from a 
[hexagonal architecture](http://alistair.cockburn.us/Hexagonal+architecture) point of view - it is unaware of the nature 
of "external" interactions. This includes requests made to the application and dependencies that the application has on 
third-party systems (including data storage).

### Dependencies
Depends on the _persist_ module.