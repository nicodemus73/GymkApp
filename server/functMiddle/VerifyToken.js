const jwt = require('jsonwebtoken');

//funcion del middleware para comprobar la sesion
module.exports = function verifyToken (req, res, next) {

    const token = req.header('Authorization');
    if (!token) return res.status(401).send('Access Denied'); //no token provided

    try {
        const verified = jwt.verify(token, "dfsdkhnsdmvnkdjvn"/*process.env.nomvariabletoken*/);
        req.usernameId = verified;
        next();

    } catch(error) {
        res.status(400).send('Invalid Token');
    }
}