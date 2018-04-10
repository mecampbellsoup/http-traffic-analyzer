## `http-traffic-analyzer`

For a given logfile which contains HTTP access logs, i.e. HTTP requests broken down by user and page requested,
generate the following by-day summary statistics:

* pages by unique hits (i.e. greatest number of total requests)
* pages by number of users (i.e. greatest number of unique users requested the page)
* users by unique page views (i.e. user with most unique page views ranked highest)

```
Path,User,Timestamp
/index.html,04a5d9a7-0a76-47a8-abd3-9e39a1abce50,2017-09-28T19:38:59+00:00
```

### Methodology

1. Parse the logs `http-access-logs.csv`, and write a `Request` record (associated with both a `User` record and a `Path` record) to the DB for each row in the logfile.
1. Use the functions in `Calculator` class to generate summary statistics, e.g. the below [sample output](#sample-output). See those queries for more detail.

### Running the application

```bash
gradle run --info
```

### Sample output

Using [this test data](https://github.com/mecampbellsoup/http-traffic-analyzer/blob/master/http-access-logs.csv), running the main program will output results equal to: 
```
PageByUniqueRequestsResult(day=2018-04-07, name=/profile.html, numUsers=2, rank=1)
PageByUniqueRequestsResult(day=2018-04-07, name=/store.html, numUsers=2, rank=1)
PageByUniqueRequestsResult(day=2018-04-07, name=/index.html, numUsers=1, rank=3)
PageByUniqueRequestsResult(day=2018-04-08, name=/index.html, numUsers=2, rank=1)
PageByUniqueRequestsResult(day=2018-04-08, name=/profile.html, numUsers=1, rank=2)
PageByUniqueRequestsResult(day=2018-04-08, name=/store.html, numUsers=1, rank=2)
PageByUniqueRequestsResult(day=2018-04-09, name=/index.html, numUsers=2, rank=1)
PageByUniqueRequestsResult(day=2018-04-09, name=/profile.html, numUsers=1, rank=2)
PageByUniqueRequestsResult(day=2018-04-09, name=/store.html, numUsers=1, rank=2)
UserPageViewsPerDayResult(day=2018-04-07, userId=9, numDistinctPathsViewed=2, rank=1)
UserPageViewsPerDayResult(day=2018-04-07, userId=8, numDistinctPathsViewed=2, rank=1)
UserPageViewsPerDayResult(day=2018-04-07, userId=7, numDistinctPathsViewed=1, rank=3)
UserPageViewsPerDayResult(day=2018-04-08, userId=9, numDistinctPathsViewed=2, rank=1)
UserPageViewsPerDayResult(day=2018-04-08, userId=8, numDistinctPathsViewed=1, rank=2)
UserPageViewsPerDayResult(day=2018-04-08, userId=7, numDistinctPathsViewed=1, rank=2)
UserPageViewsPerDayResult(day=2018-04-09, userId=9, numDistinctPathsViewed=2, rank=1)
UserPageViewsPerDayResult(day=2018-04-09, userId=7, numDistinctPathsViewed=1, rank=2)
UserPageViewsPerDayResult(day=2018-04-09, userId=8, numDistinctPathsViewed=1, rank=2)
PageByMostUniqueRequests(path=/store.html, day=2018-04-07, userId=9, pageViewCount=286, rank=1)
PageByMostUniqueRequests(path=/profile.html, day=2018-04-07, userId=9, pageViewCount=220, rank=2)
PageByMostUniqueRequests(path=/index.html, day=2018-04-07, userId=8, pageViewCount=154, rank=3)
PageByMostUniqueRequests(path=/store.html, day=2018-04-07, userId=8, pageViewCount=110, rank=4)
PageByMostUniqueRequests(path=/profile.html, day=2018-04-07, userId=7, pageViewCount=88, rank=5)
PageByMostUniqueRequests(path=/store.html, day=2018-04-08, userId=9, pageViewCount=286, rank=1)
PageByMostUniqueRequests(path=/index.html, day=2018-04-08, userId=8, pageViewCount=264, rank=2)
PageByMostUniqueRequests(path=/profile.html, day=2018-04-08, userId=9, pageViewCount=220, rank=3)
PageByMostUniqueRequests(path=/index.html, day=2018-04-08, userId=7, pageViewCount=88, rank=4)
PageByMostUniqueRequests(path=/store.html, day=2018-04-09, userId=9, pageViewCount=286, rank=1)
PageByMostUniqueRequests(path=/index.html, day=2018-04-09, userId=8, pageViewCount=264, rank=2)
PageByMostUniqueRequests(path=/profile.html, day=2018-04-09, userId=9, pageViewCount=220, rank=3)
PageByMostUniqueRequests(path=/index.html, day=2018-04-09, userId=7, pageViewCount=88, rank=4
```
