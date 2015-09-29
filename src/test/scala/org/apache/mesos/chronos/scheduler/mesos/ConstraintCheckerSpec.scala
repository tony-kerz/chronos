package org.apache.mesos.chronos.scheduler.mesos

import mesosphere.mesos.protos._
import org.apache.mesos.chronos.scheduler.jobs.constraints.ConstraintSpecHelper
import org.apache.mesos.Protos
import java.util.logging.Logger
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit
import org.apache.mesos.chronos.scheduler.jobs.constraints.LikeConstraint
import org.apache.mesos.chronos.scheduler.jobs.constraints.EqualsConstraint
import mesosphere.mesos.protos.Implicits._
import org.specs2.specification.BeforeEach

/**
 * spec for testing ConstraintChecker
 *
 * @author tony kerz (anthony.kerz@gmail.com)
 */
class ConstraintCheckerSpec extends SpecificationWithJUnit 
with Mockito 
with ConstraintSpecHelper 
{
  isolated
  
  val offer = Protos.Offer.newBuilder()
  .setId(OfferID("1"))
  .setFrameworkId(FrameworkID("chronos"))
  .setSlaveId(SlaveID("slave-1"))
  .setHostname("slave.one.com")
  .addAttributes(createTextAttribute("rack", "rack-1"))
  .build()
  
   val offerWithHostname = Protos.Offer.newBuilder()
  .setId(OfferID("1"))
  .setFrameworkId(FrameworkID("chronos"))
  .setSlaveId(SlaveID("slave-1"))
  .setHostname("slave.one.com")
  .addAttributes(createTextAttribute("hostname", "slave.explicit.com"))
  .build()
      
  val constraintChecker = new ConstraintChecker()
  
  "check constraints" should {

    "be true when equal" in {
      val constraints = Seq(EqualsConstraint("rack", "rack-1"))
      constraintChecker.checkConstraints(offer, constraints) must beTrue
    }

    "be false when not equal" in {
      val constraints = Seq(EqualsConstraint("rack", "rack-2"))
      constraintChecker.checkConstraints(offer, constraints) must beFalse
    }
    
    "be true when like" in {
      val constraints = Seq(LikeConstraint("rack", "rack-[1-3]"))
      constraintChecker.checkConstraints(offer, constraints) must beTrue
    }
    
    "be false when not like" in {
      val constraints = Seq(LikeConstraint("rack", "rack-[2-3]"))
      constraintChecker.checkConstraints(offer, constraints) must beFalse
    }
    
    "be true when hostname equal" in {
      val constraints = Seq(EqualsConstraint("hostname", "slave.one.com"))
      constraintChecker.checkConstraints(offer, constraints) must beTrue
    }
    
    "be false when hostname not equal" in {
      val constraints = Seq(EqualsConstraint("hostname", "slave.two.com"))
      constraintChecker.checkConstraints(offer, constraints) must beFalse
    }
    
    "be false when hostname explicitly set to something else and not equal" in {
      val constraints = Seq(EqualsConstraint("hostname", "slave.one.com"))
      constraintChecker.checkConstraints(offerWithHostname, constraints) must beFalse
    }
    
    "be true when hostname explicitly set to something else and equal" in {
      val constraints = Seq(EqualsConstraint("hostname", "slave.explicit.com"))
      constraintChecker.checkConstraints(offerWithHostname, constraints) must beTrue
    }
  }
}
