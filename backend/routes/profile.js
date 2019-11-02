const express = require('express')
const router = express.Router()

const azureServerURL = "https://ingredishare-backend.azurewebsites.net"
const localServerURL = "http://localhost:1337"

router.get('/', (req, res) => {
    res.send("Hello world!");
});

router.get('/getProfileInfo', (req, res) => {
    let requestURL = req.url;
    const currentURL = new URL(azureServerURL+requestURL);
    const search_params = currentURL.searchParams;
    var emailObj = {email : search_params.get('email')};

    try {
        getProfileInfo(emailObj).then((result) => {
            res.json(result);
        });
    } catch (err) {
    }
});

async function getProfileInfo(obj) {
    const result = await dbIngrediShare.collection("users").find({email: obj.email}, {projection: {displayName: 1, bio: 1, preferences: 1, radius_preference: 1}}).toArray();
    return result;
}

router.post('/updateProfileInfo', (req, res) => {
    try {
        var myquery = { email: req.body.email };
        var newvalues = { $set: {displayName: req.body.displayName, bio: req.body.bio,
                preferences: req.body.preferences, radius_preference: req.body.radius_preference} };
        
        dbIngrediShare.collection("users").updateOne(myquery, newvalues, function(err, res) {
        if (err) throw err;
        });
        res.json({"updateProfileInfo": true});
    } catch (err){
        res.json({"updateProfileInfo": false});
        throw err;
    }
});

module.exports = router