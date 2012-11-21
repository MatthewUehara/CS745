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
/** Signature effect for controled access **/
abstract sig Effect {}

/** possible effect from XACML **/
one sig Permit, Deny, NotApplicable, Indeterminate extends Effect {}

/** Signature rule for controled access **/
abstract sig Rule {
  ruleTarget : one Target,
  ruleEffect : one Effect
}


/** Signature Policy controled access**/
abstract sig Policy {
  policyTarget : one Target,
  rules : set Rule,
  combiningAlgo : one CombiningAlgo
} 

/** Signature policy set controled access **/
abstract sig PolicySet {
  policySetTarget : one Target,
  policies : set Policy,
  policySets : set PolicySet,
  combiningAlgo : one CombiningAlgo
}

/** Signature Algorithmes de combinaison **/
abstract sig CombiningAlgo {}

/** Types of combining algorithm **/
one sig PermitOverrides, DenyOverrides extends CombiningAlgo {}


pred targetMatch [t : Target, q : Request] {
  some s: t.subjects | elementMatch[q.subject, s]
  some r: t.resources | elementMatch[q.resource, r]
  some a: t.actions | elementMatch[q.action, a]
}

pred elementMatch[e1: Element, e2 : Element]{
  all a2 : e2.attributes.Value
    {some a1 : e1.attributes.Value 
       { a1=a2 and a2.(e2.attributes) in a1.(e1.attributes) }}
}



fun ruleResponse (r : Rule, q : Request) : Effect {
  targetMatch[r.ruleTarget, q] => r.ruleEffect
  else NotApplicable
}

pred existsDeny[p : Policy, q : Request] {
  some r : p.rules | ruleResponse[r, q] = Deny
}

pred existsPermit[p : Policy, q : Request] {
  some r : p.rules | ruleResponse[r, q] = Permit
}

fun rulePermitOverrides ( p : Policy, q : Request) : Effect {
  existsPermit[p,q] => Permit
  else existsDeny[p,q] => Deny // check this!!!!! (this one is if...else if...else)
  else NotApplicable
}

fun ruleDenyOverrides ( p : Policy, req : Request) : Effect {
  existsDeny[p,req]=> Deny
  else existsPermit[p,req] =>Permit //same here and below
  else NotApplicable
}
fun policyResponse (p : Policy, req : Request) : Effect {
  targetMatch[p.policyTarget, req]=>ruleCombinedResponse[p, req]
  else NotApplicable
}

fun ruleCombinedResponse (p : Policy, req : Request) : Effect {
  p.combiningAlgo = PermitOverrides => rulePermitOverrides[p, req]
  else p.combiningAlgo = DenyOverrides => ruleDenyOverrides[p, req]
  else Indeterminate    
}

pred InconsistentPolicy [p : Policy, req : Request]{
	some r : p.rules | ruleResponse[r, req] = Permit
	some r : p.rules | ruleResponse[r, req] = Deny
}

assert RulesConsistency {
	no p : Policy, req : Request | InconsistentPolicy[p,req]
}


one sig Read, Modify extends Value {}

one sig MarksFile extends Value {}

one sig Student, Professor extends Value {}

one sig ActionName extends Attribute {}{
  values = Read + Modify
}

one sig Role extends Attribute {}{
  values = Student + Professor
}

one sig ResourceName extends Attribute {}{
  values = MarksFile
}

fact{
  Subject.attributes.Value = Role
  Resource.attributes.Value = ResourceName
  Action.attributes.Value = ActionName
}

one sig S1 extends Subject{}{
  attributes = Role -> Professor
}
one sig S2 extends Subject{}{
  attributes = Role -> Student
}

/*
one sig S3 extends Subject{}{
  attributes = Role -> (Student + Professor)
}*/

one sig R1 extends Resource{}{
  attributes = ResourceName ->  MarksFile
}

one sig A1 extends Action{}{
  attributes = ActionName -> Read
}

one sig A2 extends Action{}{
  attributes = ActionName -> Modify
}

one sig T0 extends Target {}{
  subjects = S1 + S2
  resources = R1
  actions = A1 + A2
}

one sig T1 extends Target {}{
  subjects = S1
  resources = R1
  actions = A1 + A2
}

one sig T2 extends Target {}{
  subjects = S2
  resources = R1
  actions = A1
}

one sig T3 extends Target {}{
  subjects = S2
  resources = R1
  actions = A2
}


one sig Rule1 extends Rule {}{
  ruleTarget = T1
  ruleEffect = Permit
}


one sig Rule2 extends Rule {}{
  ruleTarget = T2
  ruleEffect = Permit
}


one sig Rule3 extends Rule {}{
  ruleTarget = T3
  ruleEffect = Deny
}

one sig P extends Policy {}
{
  policyTarget = T0
  rules = Rule1 + Rule2 + Rule3
  combiningAlgo = DenyOverrides
}

fact {
  one Request.action.attributes
}

pred PermitPolicy[q : Request, p : Policy]{
  policyResponse[p,q] = Permit
}

pred DenyPolicy[q : Request, p : Policy]{
  policyResponse[p,q] = Deny
}

run InconsistentPolicy for 8 but 1 Request

check RulesConsistency for 8 but 1 Request


