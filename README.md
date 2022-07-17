### Problem Statement
Write a scalable microservice in Scala, which exposes an API endpoint that takes a list of URLs, crawls the
data concurrently, and returns the crawled data.

### API Details
```
endpoint: /api/crawl
http method: post
request body:

{
    "urls": ["https://google.com", "https://github.com"]
}

response body:

{
    "result": [
        {
            "url": "https://google.com",
            "data": "..."
        },
        {
            "url": "https://github.com",
            "data": "..."
        }
    ],
    "error": null
}
```
### Evaluation Criteria

We are looking for people who write production grade code.
* Readable and clean code: 20%
* Code structuring: 20%
* Code Test: 20%
* Code Documentation: 20%
* Code Tooling(Dockerize, etc): 10%
* Problem Solving: 10%

### Some Tips
- Use a version control system and commit often
- Make your code extensible (e.g. is it easy to add a caching layer?)
