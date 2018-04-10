package http.traffic.analyzer

import http.traffic.analyzer.models.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.io.File
import java.io.InputStream


// NOTE: assumes you have a PostgreSQL server running at localhost:5432
// and a database called 'http-traffic-analyzer' created.
// TODO: make this configurable.
const val dbUser = "mec"
const val dbUri = "jdbc:postgresql://localhost:5432/http-traffic-analyzer?user=$dbUser"

fun main(args: Array<String>) {
    // Setup the database
    Database.connect(dbUri, driver = "org.postgresql.Driver")

    // Locate the logfile
    val path = "http-access-logs.csv"

    transaction {
        // print sql to std-out
        logger.addLogger(StdOutSqlLogger)

        // create the tables
        create(Requests, Paths, Users)

        parsing@ for (entry in accessLogs(path)) {
            val parsedEntry = parseEntry(entry)
            if (parsedEntry.first() == "Path") continue@parsing // skip the headers

            // first find or create the path and user entries
            val p: Path = findOrCreatePath(parsedEntry[0])
            val u: User = findOrCreateUser(parsedEntry[1].toInt())

            // then create the request records
            val r = Request.new {
                this.timestamp = DateTime.parse(parsedEntry[2])
                this.path = p
                this.user = u
            }

            println("Request created with path ${r.path.name} (${r.path.id}), user ${r.user.uid} (${r.user.id}), and timestamp ${r.timestamp}.")
        }
    }

    // Now we can run the Calculator
    val calculator = Calculator()

    val mostViewedPagesPerDayResult = calculator.pagesByUniqueRequestsCountPerDay()
    for (row in mostViewedPagesPerDayResult) {
        println(row)
    }

    val mostUserPageViewsByUserPerDayResult = calculator.usersByMostPageViewsPerDay()
    for (row in mostUserPageViewsByUserPerDayResult) {
        println(row)
    }

    val mostUniquePageViewsPerDayResult = calculator.pagesByMostUniqueViewsPerDay()
    for (row in mostUniquePageViewsPerDayResult) {
        println(row)
    }
}

private fun accessLogs(path: String): List<String> {
    val inputStream: InputStream = File(path).inputStream()
    val lineList = mutableListOf<String>()

    inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it) } }
    return lineList
}

private fun parseEntry(entry: String): List<String> {
    return entry.split(",")
}
