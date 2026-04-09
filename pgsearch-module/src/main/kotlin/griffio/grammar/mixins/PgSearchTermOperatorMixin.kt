package griffio.grammar.mixins

import com.alecstrong.sql.psi.core.psi.SqlBinaryExpr
import com.alecstrong.sql.psi.core.psi.SqlCompositeElementImpl
import com.alecstrong.sql.psi.core.psi.SqlExpr
import com.intellij.lang.ASTNode
import griffio.grammar.psi.PgSearchTermOperator

/**
 * Used for `x === y` expressions in SqlBinaryExpr type resolver
 * https://docs.paradedb.com/documentation/full-text/term
 */
internal abstract class PgSearchTermOperatorMixin(node: ASTNode) :
    SqlCompositeElementImpl(node),
    SqlBinaryExpr,
    PgSearchTermOperator {

    override fun getExprList(): List<SqlExpr> {
        return children.filterIsInstance<SqlExpr>()
    }
}
