package org.javers.core

import org.javers.core.diff.DiffFactory
import org.javers.core.metamodel.property.BeanBasedPropertyScanner
import org.javers.core.metamodel.property.EntityManager
import org.javers.core.metamodel.property.FieldBasedPropertyScanner
import org.javers.core.metamodel.property.PropertyScanner
import org.javers.core.model.DummyNetworkAddress
import org.javers.model.mapping.type.TypeMapper
import spock.lang.Specification
import spock.lang.Unroll

import javax.persistence.Id

import static org.fest.assertions.api.Assertions.assertThat
import static org.javers.core.JaversBuilder.*

/**
 * @author bartosz walacik
 */
class JaversBuilderTest extends Specification {

    def "should load default properties file"() {
        given:
        JaversBuilder javersBuilder = javers()

        when:
        javersBuilder.build()

        then:
        javersBuilder.getContainerComponent(PropertyScanner) instanceof FieldBasedPropertyScanner
    }

    
    def "should manage Entity"() {
        when:
        Javers javers = javers().registerEntity(DummyEntity).build()

        then:
        javers.isManaged(DummyEntity)
    }

    
    def "should manage ValueObject"() {
        when:
        Javers javers = javers().registerValueObject(DummyNetworkAddress).build()

        then:
        javers.isManaged(DummyNetworkAddress)
    }

    
    def "should create Javers"() {
        when:
        Javers javers = javers().build()

        then:
        javers != null
    }

    
    def "should create multiple Javers instances"() {
        when:
        Javers javers1 = javers().build()
        Javers javers2 = javers().build()

        then:
        javers1 != javers2
    }

    def "should contain FieldBasedPropertyScanner when Field style"(){
        given:
        JaversBuilder javersBuilder = javers().withMappingStyle(MappingStyle.FIELD)

        when:
        javersBuilder.build()

        then:
        javersBuilder.getContainerComponent(PropertyScanner) instanceof FieldBasedPropertyScanner
    }

    
    def "should contain BeanBasedPropertyScanner when Bean style"(){
        given:
        JaversBuilder javersBuilder = javers().withMappingStyle(MappingStyle.BEAN)

        when:
        javersBuilder.build()

        then:
        javersBuilder.getContainerComponent(PropertyScanner) instanceof BeanBasedPropertyScanner
    }

    
    def "should not contain FieldBasedPropertyScanner when Bean style"() {
        given:
        JaversBuilder javersBuilder = javers().withMappingStyle(MappingStyle.BEAN)

        when:
        javersBuilder.build()

        then:
        javersBuilder.getContainerComponent(FieldBasedPropertyScanner) == null
    }

    
    def "should not contain BeanBasedPropertyScanner when Field style"(){
        given:
        JaversBuilder javersBuilder = javers().withMappingStyle(MappingStyle.FIELD)

        when:
        javersBuilder.build()

        then:
        javersBuilder.getContainerComponent(BeanBasedPropertyScanner) == null
    }


    def "should create multiple javers containers"() {
        given:
        JaversBuilder builder1 = JaversBuilder.javers()
        JaversBuilder builder2 = JaversBuilder.javers()

        when:
        builder1.build()
        builder2.build()

        then:
        builder1.getContainerComponent(EntityManager) != builder2.getContainerComponent(EntityManager)
    }

    @Unroll
    def "should contain #clazz.getSimpleName() bean"() {
        given:
        JaversBuilder builder = javers()

        when:
        builder.build()

        then:
        assertThat(builder.getContainerComponent(clazz)) isInstanceOf(clazz)

        where:
        clazz << [Javers, EntityManager, TypeMapper, DiffFactory]
    }

    def "should contain singletons"() {
        given:
        JaversBuilder builder = javers()

        when:
        builder.build()

        then:
        builder.getContainerComponent(Javers) == builder.getContainerComponent(Javers)
    }

    class DummyEntity {
        @Id
        int id
        DummyEntity parent
    }
}
