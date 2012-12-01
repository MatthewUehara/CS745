
//===============================================
// Access Policy Model: version 2, supports overriding
// Date: November 8
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

//INC1 FIX:
//fact
//{
//	#Request.subject.attributes = 1
//}

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
  combiningAlgo : one PolicyCombiningAlgo
} 

/** Signature policy set controled access **/
abstract sig PolicySet {
  policySetTarget : one Target,
  policies : set Policy,
  policySets : set PolicySet,
  combiningAlgo : one PolicyCombiningAlgo
}

/** Signatures of Types of combining algorithm **/

abstract sig CombiningAlgo {}
abstract sig PolicyCombiningAlgo {}

/** Types of combining algorithm **/
one sig PermitOverrides, DenyOverrides extends CombiningAlgo {}
one sig P_PermitOverrides, P_DenyOverrides, P_OnlyOneApplicable extends PolicyCombiningAlgo {}

// PREDICATES

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

// for permit-overrides
fun rulePermitOverrides ( p : Policy, q : Request) : Effect {
  existsPermit[p,q] => Permit
  else existsDeny[p,q] => Deny
  else NotApplicable
}

// for deny-overrides
fun ruleDenyOverrides ( p : Policy, req : Request) : Effect {
  existsDeny[p,req]=> Deny
  else existsPermit[p,req] =>Permit
  else NotApplicable
}

// for only-one-applicable
fun ruleOnlyOneApplicable( p : Policy, req : Request) : Effect {
  (existsDeny[p,req] && existsPermit[p,req]) => Indeterminate
  else existsPermit[p,req] =>Permit
  else existsDeny[p,req] =>Deny
  else NotApplicable
}

fun policyResponse (p : Policy, req : Request) : Effect {
  (p.combiningAlgo = P_PermitOverrides) => rulePermitOverrides[p, req]
  else (p.combiningAlgo = P_DenyOverrides) => ruleDenyOverrides[p, req]
  else (p.combiningAlgo = P_OnlyOneApplicable) => ruleOnlyOneApplicable[p, req]
  else NotApplicable    
}


// CONCRETE MODEL



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

one sig T4 extends Target {}{
  subjects = S1
  resources = R1
  actions = A1 + A2
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

one sig Rule4 extends Rule {}{
  ruleTarget = T4
  ruleEffect = Deny
}

one sig P3 extends Policy {}{
  policyTarget = T0
  rules = Rule1 + Rule3 + Rule4
  combiningAlgo = P_OnlyOneApplicable
}

one sig P2 extends Policy {}{
  policyTarget = T0
  rules = Rule2
  combiningAlgo = P_OnlyOneApplicable
}

one sig PS extends PolicySet{}{
  policySetTarget = T0
  combiningAlgo = P_DenyOverrides
  policies = P3 + P2
}

fact {
  one Request.subject.attributes
}

// PREDICATES FOR RUNNING

//pred InconsistentPolicyRules [req : Request]{
//	some r : P3.rules | ruleResponse[r, req] = Permit
//	some r : P3.rules | ruleResponse[r, req] = Deny
//}

//pred InconsistentPolicySet [ps : PolicySet, req : Request]{
//	some p : ps.policies | policyResponse[p, req] = Indeterminate
//}

pred InconsistentPolicySet [ps : PolicySet, req : Request, r1: Rule, r2: Rule]{
	some p : ps.policies | 
		r1 in p.rules and r2 in p.rules 
		and p.combiningAlgo = P_OnlyOneApplicable 
		and ruleResponse[r1, req] = Deny
		and ruleResponse[r2, req] = Permit
}

// PolicySet checking
run InconsistentPolicySet

// Policy checking
//run InconsistentPolicyRules for 8 but 8 Request
