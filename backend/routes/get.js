const express = require('express')
const router = express.Router()

router.get('/', (req, res) => {
    res.send("Hello world!");
});

router.get('/getAllRequests', (req, res) => {
    try {
        getAllRequests().then((result) => {
            res.writeHead(200, {"Content-Type": "text/plain"});
            var str = "";
            result.forEach(function (item) {
                str += JSON.stringify(item);
            });
            res.end(str);
        });
    } catch (err) {
    }
});

async function getAllRequests() {
    const result = await dbIngrediShare.collection("requests").find({}).toArray();
    return result;
}

router.post('/createRequest', (req, res) => {
    try {
        var newReq = {name: req.body.name, quantity: req.body.quantity};
        dbIngrediShare.collection("requests").insertOne(newReq, function(err,res) {
            if(err) throw err;
        })
        res.json("inserted "+ newReq.name + " and "+ newReq.quantity + " successfully");
    } catch (err){}
});

router.get('/isExistingUser', (req, res) => {
    console.log("/isExistingUser GET");

    var user_email = req.body.email;

    var query = dbIngrediShare.collection("users").find({email:user_email}).toArray(function(err, result) {
        if(err) throw err;

        if(typeof result !== 'undefined' && result.length > 0) {
            res.json({"pre_existing_user" : true});
        } else {
            res.json({"pre_existing_user" : false});

            /* TODO: save to db */
            var newUser = {email : user_email};
            dbIngrediShare.collection("users").insertOne(newUser, function(err, res) {
                if(err) throw err;

                console.log("Created new user!");
            });
        }
    });
});

module.exports = router