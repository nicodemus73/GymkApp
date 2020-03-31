const jwt = require('jsonwebtoken');

//funcion del middleware para comprobar la sesion
module.exports = function verifyToken (req, res, next) {

    const token = req.header('Authorization');
    if (!token) return res.json({ "error": 'Access Denied, no token provided' }); //no token provided

    try {
        const verified = jwt.verify(token, "dfsdkhnsdmvnkdjvn"/*process.env.nomvariabletoken*/);
        req.usernameId = verified;
        next();

    } catch(error) {
        res.json({"error":'Invalid Token'});
    }
}