const express = require('express');
const router = express.Router();

const Usuario = require('../bbdd/UserSchema');
//nomes entra si se li pasa el token pel header Authorization <valor>  es el numero que surt quan fas loggin
router.get('/', (req, res) => {
    //obtenim l'usuari
    Usuario.findById(req.usernameId, function (err, user) {
        if (err) return res.status(500).send("There was a problem finding the user.");
        if (!user) return res.status(404).send("No user found.");

        res.status(200).send(user);
    });
    /*res.json({
        post: {
            title: 'Prova',
            description: 'Aixo es una prova'
        }
    });
*/
    //res.send(req.username);
});

router.post('/', (req, res) => {
    //obtenim l'usuari
    Usuario.findById(req.usernameId, function (err, user) {
        if (err) return res.status(500).send("There was a problem finding the user.");
        if (!user) return res.status(404).send("No user found.");

        res.status(200).send(user);
    });
    /*res.json({
        post: {
            title: 'Prova',
            description: 'Aixo es una prova'
        }
    });
*/
    //res.send(req.username);
});

module.exports = router;