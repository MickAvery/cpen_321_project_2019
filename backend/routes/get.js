const express = require("express");
const router  = new express.Router();
const dbObj   = require("../index.js");

const azureServerURL = "https://ingredishare-backend.azurewebsites.net";
const localServerURL = "http://localhost:1337";

async function getAllRequests() {
    const result = await dbObj.collection("requests").find({}).toArray();
    return result;
}

router.get("/getAllRequests", (req, res) => {
    getAllRequests().then((result) => {
        res.json(result);
    });
});

async function getAllRequestsFromLatLong(temp) {
    const radius = await dbObj.collection("users").find(
        {email: temp.email},
        {projection: {radius_preference: 1}}
    ).toArray();
    var radiusPref = (radius.length > 0)? ((radius[0].radius_preference)? radius[0].radius_preference : 0) : 0;
    var latRange = Number(radiusPref) * (Number(1) / Number(110.574));
    var longRange = Number(radiusPref) * (Number(1) / (Number(111.320) * Math.cos(temp.lat)));

    const result = await dbObj.collection("requests").find({
        lat: { $gt: (Number(temp.lat)-Number(latRange)), $lt: (Number(temp.lat)+Number(latRange))},
        long: { $gt: (Number(temp.long)-Number(longRange)), $lt: (Number(temp.long)+Number(longRange))},
        userId: temp.email
    }).toArray();

    return result;
}

router.get("/getAllRequestsFromLatLong", (req, res) => {
    let requestURL = req.url;

    const currentURL = new URL(azureServerURL + requestURL);
    const searchParams = currentURL.searchParams;

    var temp = {
        lat: searchParams.get("lat"),
        long: searchParams.get("long"),
        email: searchParams.get("email")
    };

    try {
        getAllRequestsFromLatLong(temp).then((result) => {
            res.json(result);
        });
    } catch (err) {
        res.status(500).end();
    }
});

router.post("/createRequest", (req, res) => {
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
        dbObj.collection("requests").insertOne(newReq, function(err,res) {
            if(err){
                res.json({"createRequestResponse": false});
                throw err;
            } 
        });
        res.json({"createRequestResponse": true});
    } catch (err) {
        res.status(500).end();
    }
});

router.get("/isExistingUser", (req, res) => {
    var userEmail = req.body.email;

    var query = dbObj.collection("users").find({email:userEmail}).toArray(function(err, result) {
        if(err) throw err;

        if(typeof result !== "undefined" && result.length > 0) {
            res.json({"pre_existing_user" : true});
        } else {
            res.json({"pre_existing_user" : false});

            var newUser = {email : userEmail};
            dbObj.collection("users").insertOne(newUser, function(err, res) {
                if(err) {
                    res.status(500).end();
                }
            });
        }
    });
});

module.exports = router;
