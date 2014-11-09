# Core services

A Spring (Maven) module that offers services for all the core functionality of the application.

# Notes
1. Currently this module includes all the code related to persistent storage of data. It would be better to factor this out into another module called _integration_ or something when the core functionality grows.
2. Abstract somehow from the `MONGOLAB_URI` environment variable. 