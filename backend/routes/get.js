const express = require('express')
const router = express.Router()

const azureServerURL = "https://ingredishare-backend.azurewebsites.net"
const localServerURL = "http://localhost:1337"

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
    let requestURL = req.url;

    const currentURL = new URL(azureServerURL + requestURL);
    const search_params = currentURL.searchParams;

    var temp = {
        lat: search_params.get('lat'),
        long: search_params.get('long')
    };

    try {
        getAllRequestsFromLatLong(temp).then((result) => {
            res.json(result);
        });
    } catch (err) {
    }
});

async function getAllRequestsFromLatLong(temp) {
    // const result = await dbIngrediShare.collection("requests").find({lat: Number(temp.lat), long: Number(temp.long)}).toArray();
    // db.student.find({ u1 : { $gt :  30, $lt : 60}});
    const result = await dbIngrediShare.collection("requests").find({
        lat: { $gt: Number(temp.lat - 5), $lt: Number(temp.lat + 5)},
        long: { $gt: Number(temp.long - 5), $lt: Number(temp.long + 5)},
    }).toArray();
    return result;
}

router.post('/createRequest', (req, res) => {
    try {
        var newReq = {
            name: req.body.name, 
            description: req.body.description, 
            lat: req.body.lat, 
            long: req.body.long,
            userId: req.body.userId,
            type: req.body.type
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