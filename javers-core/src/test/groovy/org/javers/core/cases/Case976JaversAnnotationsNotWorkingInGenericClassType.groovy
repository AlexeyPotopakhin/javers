package org.javers.core.cases

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.DiffIgnore
import spock.lang.Specification

/**
 * https://github.com/javers/javers/issues/976
 */

class R {
    private UUID id

    UUID getId() {
        return id
    }

    void setId(UUID id) {
        this.id = id
    }
}

class N extends R {
    @DiffIgnore
    private String name

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        N n = (N) o

        if (name != n.name) return false

        return true
    }

    int hashCode() {
        return (name != null ? name.hashCode() : 0)
    }
}

abstract class A<T extends R> {
    private List<T> rows = new ArrayList<>()

    List<T> getRows() {
        return rows
    }

    void setRows(List<T> rows) {
        this.rows = rows
    }
}

class B extends A<N> {

}

class Case976JaversAnnotationsNotWorkingInGenericClassType extends Specification {
    def "should not detect any changes"() {
        given:
        Javers javers = JaversBuilder.javers().build()

        B B1 = new B(rows: [new N( name: "name")])

        B B2 = new B(rows: [new N( name: "name")])

        when:
        def diff = javers.compare(B1, B2)
        println("diff: " + diff.prettyPrint())

        then:
        diff.getChanges().size() == 0
    }

    def "should not detect any changes with annotated property with @DiffIgnore"() {
        given:
        Javers javers = JaversBuilder.javers().build()

        B B1 = new B(rows: [new N( name: "name")])

        B B2 = new B(rows: [new N( name: "some other name")])

        when:
        def diff = javers.compare(B1, B2)
        println("diff: " + diff.prettyPrint())

        then:
        diff.getChanges().size() == 0
    }
}
