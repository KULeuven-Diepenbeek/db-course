const fs = require('fs')
const path = require('path')

console.log('reading courses.json (curl -X GET http://127.0.0.1:5984/courses/_all_docs\?include_docs\=true > courses.json)')

const data = JSON.parse(fs.readFileSync('courses.json'));
data.docs = data.rows.map(row => {
	delete row.doc._rev
	return row.doc
})
delete data.rows

fs.writeFileSync('dump.db', JSON.stringify(data))
console.log('done in dump.db')
console.log('curl -d @dump.db -H "Content-Type: application/json" -X POST http://127.0.0.1:5984/brol/_bulk_docs')
