package io.github.rajeev02.kmpapp001

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.request.receive
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json { prettyPrint = true })
    }
    routing {

        val items = ConcurrentHashMap<Int, Item>()

        // Initializing default data
        items[1] = Item(id = 1, name = "Item One", description = "The first item")
        items[2] = Item(id = 2, name = "Item Two", description = "The second item")
        items[3] = Item(id = 3, name = "Item Three", description = "The third item")

        var idCounter = 4  // Start counter from 4 since we have 3 default items

        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }

        get("/hello") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }

        get("/json") {
            val sampleResponse = SampleResponse(message = "Hello from Ktor", status = "success")
            call.respond(sampleResponse)
        }

        // GET all items
        get("/items") {
            call.respond(items.values.toList())
        }

        // GET a single item by ID
        get("/items/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val item = items[id]
            if (item == null) {
                call.respondText("Item not found", status = io.ktor.http.HttpStatusCode.NotFound)
            } else {
                call.respond(item)
            }
        }

        // POST (create) a new item
        post("/items") {
            val newItem = call.receive<Item>()
            val id = idCounter++
            val itemWithId = newItem.copy(id = id)
            items[id] = itemWithId
            call.respond(itemWithId)
        }

        // PUT (update) an existing item by ID
        put("/items/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null || !items.containsKey(id)) {
                call.respondText("Item not found", status = io.ktor.http.HttpStatusCode.NotFound)
            } else {
                val updatedItem = call.receive<Item>()
                val itemWithId = updatedItem.copy(id = id)
                items[id] = itemWithId
                call.respond(itemWithId)
            }
        }

        // DELETE an item by ID
        delete("/items/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null || items.remove(id) == null) {
                call.respondText("Item not found", status = io.ktor.http.HttpStatusCode.NotFound)
            } else {
                call.respondText("Item deleted", status = io.ktor.http.HttpStatusCode.OK)
            }
        }
    }
}


@Serializable
data class SampleResponse(val message: String, val status: String)

@Serializable
data class Item(val id: Int? = null, val name: String, val description: String)
