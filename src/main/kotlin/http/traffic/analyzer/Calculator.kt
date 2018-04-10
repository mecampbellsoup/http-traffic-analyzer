package http.traffic.analyzer

import java.sql.Date
import java.sql.ResultSet

data class UserPageViewsPerDayResult(
        val day: Date,
        val userId: Int,
        val numDistinctPathsViewed: Int,
        val rank: Int
)

data class PageByUniqueRequestsResult(
        val day: Date,
        val name: String,
        val numUsers: Int,
        val rank: Int
)

data class PageByMostUniqueRequests(
        val path: String,
        val day: Date,
        val userId: Int,
        val pageViewCount: Int,
        val rank: Int
)

fun <T : Any> String.execAndMap(transform: (ResultSet) -> T): List<T> {
    val result = arrayListOf<T>()

    val conn = java.sql.DriverManager.getConnection(dbUri)
    val statement = conn.createStatement()
    val resultSet = statement.executeQuery(this)
    while (resultSet.next()) {
        result += transform(resultSet)
    }
    return result
}

//
// Exposes public functions thath generate by-day rankings of:
//  * pages by unique hits (i.e. greatest number of total requests)
//  * pages by number of users (i.e. greatest number of unique users requested the page)
//  * users by unique page views (i.e. user with most unique page views ranked highest)
//
class Calculator {
    fun pagesByMostUniqueViewsPerDay(): List<PageByMostUniqueRequests> {
        val theSql = """
WITH daily_requests_count AS (
    SELECT
      paths.name,
      requests.timestamp :: TIMESTAMP :: DATE AS day,
      requests.user,
      count(*)                                AS count
    FROM requests
      INNER JOIN paths ON requests.path = paths.id
    GROUP BY 1, 2, 3
), rank_by_day AS (SELECT
                     *,
                     rank()
                     OVER (
                       PARTITION BY day
                       ORDER BY count DESC
                       ) AS rank
                   FROM daily_requests_count
) SELECT *
  FROM rank_by_day
  WHERE rank <= 10;
        """.trimMargin()

        return theSql.execAndMap { rs ->
            PageByMostUniqueRequests(
                    rs.getString("name"),
                    rs.getDate("day"),
                    rs.getInt("user"),
                    rs.getInt("count"),
                    rs.getInt("rank")
            )
        }
    }

    fun usersByMostPageViewsPerDay(): List<UserPageViewsPerDayResult> {
        val theSql = """
WITH daily_paths_by_user AS (SELECT
                               requests.timestamp :: TIMESTAMP :: DATE AS day,
                               requests.user,
                               count(DISTINCT (path))                  AS distinct_paths
                             FROM requests
                             GROUP BY 1, 2
), rank_by_day AS (SELECT
                     *,
                     rank()
                     OVER (
                       PARTITION BY day
                       ORDER BY distinct_paths DESC
                       ) AS rank
                   FROM daily_paths_by_user
) SELECT *
  FROM rank_by_day
  WHERE rank <= 10;
                """.trimMargin()

        return theSql.execAndMap { rs ->
            UserPageViewsPerDayResult(
                    rs.getDate("day"),
                    rs.getInt("user"),
                    rs.getInt("distinct_paths"),
                    rs.getInt("rank")
            )
        }
    }

    fun pagesByUniqueRequestsCountPerDay(): List<PageByUniqueRequestsResult> {
        val theSql = """
WITH daily_user_requests_by_path AS (SELECT
                                       requests.timestamp :: TIMESTAMP :: DATE AS day,
                                       paths.name,
                                       count(DISTINCT (requests.user))         AS num_users
                                     FROM requests
                                       INNER JOIN paths ON requests.path = paths.id
                                     GROUP BY 1, 2
), rank_by_day AS (SELECT
                     *,
                     rank()
                     OVER (
                       PARTITION BY day
                       ORDER BY num_users DESC
                       ) AS rank
                   FROM daily_user_requests_by_path
) SELECT *
  FROM rank_by_day
  WHERE rank <= 10;
                """.trimMargin()

        return theSql.execAndMap { rs ->
            PageByUniqueRequestsResult(
                    rs.getDate("day"),
                    rs.getString("name"),
                    rs.getInt("num_users"),
                    rs.getInt("rank")
            )
        }
    }
}
