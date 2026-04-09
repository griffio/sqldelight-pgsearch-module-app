package griffio

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import griffio.queries.Sample
import org.postgresql.ds.PGSimpleDataSource

private fun getSqlDriver() = PGSimpleDataSource().apply {
    setURL("jdbc:postgresql://localhost:5432/mydatabase")
    applicationName = "App Main"
    user = "myuser"
    password = "mypassword"
}.asJdbcDriver()


fun main() {
    val driver = getSqlDriver()
    val sample = Sample(driver)

    sample.searchQueries.selectMixedNumericString().executeAsList().forEach { println(it) }

    driver.execute(-1, "CALL paradedb.create_bm25_test_table(schema_name => 'public', table_name => 'items');", 0).value

    println("selectMatchDisjunction")
    sample.itemsQueries.selectMatchDisjunction().executeAsList().forEach { println(it) }
    println("selectMatchConjunction")
    sample.itemsQueries.selectMatchConjunction().executeAsList().forEach { println(it) }
    println("selectMatchConjunctionArray")
    sample.itemsQueries.selectMatchConjunctionArray().executeAsList().forEach { println(it) }
}
