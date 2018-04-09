## `http-traffic-analyzer`

For a given logfile which contains HTTP access logs, i.e. HTTP requests broken down by user and page requested,
generate the following by-day summary statistics:

* pages by unique hits (i.e. greatest number of total requests)
* pages by number of users (i.e. greatest number of unique users requested the page)
* users by unique page views (i.e. user with most unique page views ranked highest)


### Methodology

1. Parse the logs `http-access-logs.csv`, and write a `Request` record (associated with a `User` record) to the DB for each row in the logfile.
1. Use the functions in `Analysis` class to generate summary statistics, e.g.:
1. Expose the summary stat-reporting functions via CLI i.e. `java -har analyzer.jar`.
