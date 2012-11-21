module notere3
//===============================================
// Access Policy Model: version 1
// Date: November 8
// Author: Matthew Ma
//===============================================

abstract sig Value {}

abstract sig Attribute { values : set Value } 

/**Signature element **/

abstract sig Element {attributes :Attribute -> Value}{
  attributes in values
}

sig Subject, Resource, Action extends Element{}

/** Signature Request controled access**/
sig Request {
  subject : one Subject,
  resource : one Resource,
  action : one Action
}

/** Signature Target **/

abstract sig Target {
  subjects : set Subject,
  resources : set Resource,
  actions : set Action
}
/** Signature effect fpr  controled access **/
abstract sig Effect {}

/** possible effect from XACML **/
one sig Permit, Deny, NotApplicable, Indeterminate extends Effect {}

/** Signature rule for controled access **/
abstract sig Rule {
  ruleTarget : one Target,
  ruleEffect : one Effect
}
