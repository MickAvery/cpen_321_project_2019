const http = require('http');
const url = require('url');

const server = http.createServer((request, response) => {
    const reqUrl = url.parse(request.url,true);

    if(reqUrl.pathname == '/getallrequests' && request.method==='GET'){
        try {
            getAllRequests().then((result) => {
                response.writeHead(200, {"Content-Type": "text/plain"});
                var str = "";
                result.forEach(function (item) {
                    str += JSON.stringify(item);
                });
                response.end(str);
            });
            } catch (err) {   
            }
    }
    else {
        response.end("add /getallrequests to view list of requests")
    }
    
});

async function getAllRequests() {
    const MongoClient = require('mongodb').MongoClient;
    const uri = "mongodb+srv://dbrui:cpen321@cluster0-mfvd7.azure.mongodb.net/admin?retryWrites=true&w=majority";
    const db = await MongoClient.connect(uri);
    const dbo = db.db("ingrediShare");
    const result = await dbo.collection("requests").find({}).toArray();
    db.close();
    return result;
}

const port = process.env.PORT || 1337;
server.listen(port);

console.log("Server running at http://localhost:%d", port);