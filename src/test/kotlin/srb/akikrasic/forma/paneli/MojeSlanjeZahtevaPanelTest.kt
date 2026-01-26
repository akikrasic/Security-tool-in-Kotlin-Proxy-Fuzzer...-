package srb.akikrasic.forma.paneli

import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.jupiter.api.Test
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

class MojeSlanjeZahtevaPanelTest {


    @Test
    fun proveraSlanjaDaVidimoZastoNeRadiPostBody()=runTest {
        val client = HttpClient.newHttpClient()

        var s = """{ "username":"mika", "password":"kompir" }""".trimIndent()
        s="username=mika&password=kompir"
        val req = HttpRequest.newBuilder()
            .uri(URI.create("https://8523fdeecf625cbb45f52c8955db4965.ctf.hacker101.com/api/v2/user"))
            //.version(HttpClient.Version.HTTP_1_1)
            .header("Content-Type", "application/x-www-form-urlencoded")
            //.header("Accept","*/*")
            //.header("Accept-Encoding", "gzip, deflate, br")
           // .header("User-Agent", "python-requests/2.26.0")
            .POST(HttpRequest.BodyPublishers.ofString(s, StandardCharsets.UTF_8))
            //.header("Content-Length","${s.length}")
            .build()
        val response = client.send(req, HttpResponse.BodyHandlers.ofString())
        println(response)
        println(response.body())
       // val s = """{ "username":"mika", "password":"kompir" }""".trimIndent()
//        val post = HttpPost("https://83c94a620d1f3ea8d9c59d6f4cc3e3ca.ctf.hacker101.com/api/v2/user")
//        val stringEntity = StringEntity(s)
//        post.entity = stringEntity
//        post.setHeader("Content-Type", "application/json")
//        post.setHeader("Accept", "*/*")
//        val klijent = HttpClients.createDefault()
//        val response = klijent.execute(post)
//        println(response.statusLine.statusCode)
//        println(response.entity.content)

//        val client = OkHttpClient()
//        val req = Request.Builder()
//        .url("https://8523fdeecf625cbb45f52c8955db4965.ctf.hacker101.com/api/v2/user")
//            .post(s.toRequestBody("application".toMediaType()))
//            .build()
//        val resp = client.newCall(req).execute()
//        val body = resp.body?.string()
//        println(body)

    }

}