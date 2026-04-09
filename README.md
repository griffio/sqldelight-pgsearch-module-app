# SqlDelight 2.3.x Postgresql pg_search module support 

https://github.com/cashapp/sqldelight

**Experimental**

Use with SqlDelight `2.3.x` or higher   

---

SqlDelight pg_search Module support

https://github.com/paradedb/paradedb

## Usage

Instead of a new dialect or adding PostgreSql extensions into the core PostgreSql grammar e.g. https://postgis.net/ and https://github.com/pgvector/pgvector

Use a custom SqlDelight module to implement the type resolver for pgSearch operations

```kotlin
sqldelight {
    databases {
        create("Sample") {
            deriveSchemaFromMigrations.set(true)
            migrationOutputDirectory = file("$buildDir/generated/migrations")
            migrationOutputFileFormat = ".sql"
            packageName.set("griffio.queries")
            dialect(libs.sqldelight.postgresql.dialect)
            module(project(":pgsearch")) // module can be local project
            // or external dependency module("io.github.griffio:sqldelight-pgSearch-module:0.0.1")
        }
    }
}
```

`pgsearch-module` published in Maven Central https://central.sonatype.com/artifact/io.github.griffio/sqldelight-pgsearch/versions

`io.github.griffio:sqldelight-pgsearch:0.0.1`

```sql
CREATE EXTENSION IF NOT EXISTS pg_search;
```

Proximity search operator `@@@`

```sql
selectMatchProximity:
SELECT numeric_field1, numeric_field2, string_field1, string_field2
FROM mixed_numeric_string_test
WHERE content @@@ 'red';

selectMatchDisjunction:
SELECT description, rating, category
FROM items
WHERE description ||| 'running shoes';

selectMatchConjunction:
SELECT description, rating, category
FROM items
WHERE description &&& 'running shoes';

selectMatchConjunctionArray:
SELECT description, rating, category
FROM items
WHERE description &&& ARRAY['running', 'shoes'];
```

TODO
* Add more operators

---

```shell
docker run \
  --name paradedb \
  -e POSTGRES_USER=myuser \
  -e POSTGRES_PASSWORD=mypassword \
  -e POSTGRES_DB=mydatabase \
  -v paradedb_data:/var/lib/postgresql/ \
  -p 5432:5432 \
  -d \
  paradedb/paradedb:latest   
```

```shell
./gradlew build &&
./gradlew flywayMigrate
```
