const express = require('express');
const router = express.Router();

const verify = require('../functMiddle/VerifyToken');
//const Usuario = require('../bbdd/Usuario');

router.get('/', verify /*middleware*/ , (req, res) => {

    /*res.json({
        post: {
            title: 'Prova',
            description: 'Aixo es una prova'
        }
    });*/

    res.send(req.username);
});

module.exports = router;