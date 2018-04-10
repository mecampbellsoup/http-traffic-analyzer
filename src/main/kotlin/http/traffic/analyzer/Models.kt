package http.traffic.analyzer.models

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SizedIterable
import org.joda.time.DateTime

object Paths : IntIdTable() {
    val name = varchar("name", 50)
}

class Path(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Path>(Paths)

    var name by Paths.name
}

object Users : IntIdTable() {
    val uid = integer("uid")
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var uid by Users.uid
}

object Requests : IntIdTable() {
    val user = reference("user", Users)
    val path = reference("path", Paths)
    val timestamp: Column<DateTime> = datetime("timestamp")

}

class Request(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Request>(Requests)

    var path by Path referencedOn Requests.path
    var user by User referencedOn Requests.user
    var timestamp by Requests.timestamp
}

fun findOrCreateUser(id: Int): User {
    val user: SizedIterable<User> = User.find { Users.uid eq id }

    return if (user.empty()) {
        User.new {
            uid = id
        }
    } else {
        if (user.count() != 1) throw Exception("Multiple users found even though they must be distinct by `uid`!")
        user.first()
    }
}

fun findOrCreatePath(pathName: String): Path {
    val path: SizedIterable<Path> = Path.find { Paths.name eq pathName }
    return if (path.empty()) {
        Path.new {
            name = pathName
        }
    } else {
        if (path.count() != 1) throw Exception("Multiple paths found even though they must be distinct by `name`!")
        path.first()
    }
}

