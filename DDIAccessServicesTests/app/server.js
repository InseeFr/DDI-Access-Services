const jsonServer = require('json-server')
const server = jsonServer.create()
const router = jsonServer.router('./db/db.json')
const middlewares = jsonServer.defaults()
const routes = require('./rewrite/routes.json')
const intercept = require('./middleware')

server.use(middlewares)

server.use(jsonServer.rewriter(routes))

intercept(server, router)

server.use(router)

server.listen(3000, () => {
  console.log('JSON Server is running')
})