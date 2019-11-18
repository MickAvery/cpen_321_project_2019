const express = require('express');
const router  = express.Router();
var mainMod   = require("../index.js");

const azureServerURL = "https://ingredishare-backend.azurewebsites.net";
const localServerURL = "http://localhost:1337";

router.get('/', (req, res) => {
    res.send("Hello world!");
});

router.get('/getProfileInfo', (req, res) => {
    var email = req.query.email;

    try {
        getProfileInfo(email).then((result) => {
            res.json(result);
        });
    } catch (err) {
        throw err;
    }
});

async function getProfileInfo(email) {
    var dbObj = mainMod.getDb();

    const result = await dbObj.collection("users").findOne({email: email}, {projection: {displayName: 1, bio: 1, preferences: 1}});
    return result;
}

router.post("/updateProfileInfo", (req, res) => {
    var dbObj = mainMod.getDb();

    var myquery = { email: req.body.email };
    var newvalues = { $set: {displayName: req.body.displayName, bio: req.body.bio,
            preferences: req.body.preferences, radiusPreference: req.body.radiusPreference} };

    dbObj.collection("users").updateOne(myquery, newvalues, {upsert : true}, function(err, res) {
        if (err) {
            throw err
        };
    });

    res.json({"updateProfileInfo": true});
});

module.exports = {
    router: router,
    getProfileInfo: getProfileInfo
}
