const express = require('express')
const router = express.Router()

router.get('/', (req, res) => {
    res.send("Hello world!");
});

router.get('/getAllRequests', (req, res) => {
    try {
        getAllRequests().then((result) => {
            res.json(result);
        });
    } catch (err) {
    }
});

async function getAllRequests() {
    const result = await dbIngrediShare.collection("requests").find({}).toArray();
    return result;
}

router.get('/getAllRequestsFromLatLong', (req, res) => {
    try {
        getAllRequestsFromLatLong(req.body.lat, req.body.long).then((result) => {
            res.json(result);
        });
    } catch (err) {
    }
});

async function getAllRequestsFromLatLong(lat,long) {
    const result = await dbIngrediShare.collection("requests").find({lat: lat, long:long}).toArray();
    return result;
}

router.post('/createRequest', (req, res) => {
    try {
        var newReq = {
            name: req.body.name, 
            description: req.body.description, 
            lat: req.body.lat, 
            long: req.body.long,
            userId: req.body.userId
        };
        if(newReq.name === undefined || 
            newReq.description === undefined ||
            newReq.lat === undefined ||
            newReq.long === undefined ||
            newReq.userId === undefined){
                res.json({"createRequestResponse": false}); return;
            }
        dbIngrediShare.collection("requests").insertOne(newReq, function(err,res) {
            if(err){
                res.json({"createRequestResponse": false});
                throw err;
            } 
        })
        res.json({"createRequestResponse": true});
    } catch (err){}
});

router.get('/isExistingUser', (req, res) => {
    var user_email = req.body.email;

    var query = dbIngrediShare.collection("users").find({email:user_email}).toArray(function(err, result) {
        if(err) throw err;

        if(typeof result !== 'undefined' && result.length > 0) {
            res.json({"pre_existing_user" : true});
        } else {
            res.json({"pre_existing_user" : false});

            var newUser = {email : user_email};
            dbIngrediShare.collection("users").insertOne(newUser, function(err, res) {
                if(err) throw err;

                console.log("Created new user!");
            });
        }
    });
});

module.exports = router