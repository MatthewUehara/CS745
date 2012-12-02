
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
  combiningAlgo : one RuleCombiningAlgo
} 

/** Signature policy set controled access **/
abstract sig PolicySet {
  policySetTarget : one Target,
  policies : set Policy,
  policySets : set PolicySet,
  combiningAlgo : one PolicyCombiningAlgo
}

/** Signatures of Types of combining algorithm **/

abstract sig RuleCombiningAlgo {}
abstract sig PolicyCombiningAlgo {}

/** Types of combining algorithm **/
one sig PermitOverrides, DenyOverrides extends RuleCombiningAlgo {}
one sig P_PermitOverrides, P_DenyOverrides, P_OnlyOneApplicable extends PolicyCombiningAlgo {}

//==================================
// PREDICATES
//==================================

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


/* RULES */

fun ruleResponse (r : Rule, q : Request) : Effect {
  targetMatch[r.ruleTarget, q] => r.ruleEffect
  else NotApplicable
}

/* POLICIES */

fun policyResponse (p : Policy, req : Request) : Effect {
  (p.combiningAlgo = PermitOverrides) => policyPermitOverrides[p, req]
  else (p.combiningAlgo = DenyOverrides) => policyDenyOverrides[p, req]
  else NotApplicable    
}

pred policyExistsRuleDeny[p : Policy, q : Request] {
  some r : p.rules | ruleResponse[r, q] = Deny
}

pred policyExistsRulePermit[p : Policy, q : Request] {
  some r : p.rules | ruleResponse[r, q] = Permit
}

// rules have only 2 combining algorithms: PermitOverrides and DenyOverrides

fun policyPermitOverrides ( p : Policy, q : Request) : Effect {
  policyExistsRulePermit[p,q] => Permit
  else policyExistsRuleDeny[p,q] => Deny
  else NotApplicable
}

fun policyDenyOverrides ( p : Policy, req : Request) : Effect {
  policyExistsRuleDeny[p,req]=> Deny
  else policyExistsRulePermit[p,req] =>Permit
  else NotApplicable
}



/* POLICY SETS */

pred policySetExistsPolicyDeny[ps : PolicySet, q : Request] {
  some p : ps.policies | policyResponse[p, q] = Deny
}

pred policySetExistsPolicyPermit[ps : PolicySet, q : Request] {
  some p : ps.policies | policyResponse[p, q] = Permit
}

fun policySetOnlyOneApplicable( ps : PolicySet, req : Request) : Effect {
  (policySetExistsPolicyDeny[ps,req] && policySetExistsPolicyPermit[ps,req]) => Indeterminate
  else policySetExistsPolicyPermit[ps,req] =>Permit
  else policySetExistsPolicyDeny[ps,req] =>Deny
  else NotApplicable
}

fun policySetResponse (ps : PolicySet, req : Request) : Effect {
// we support only OnlyOneApplicable
  (ps.combiningAlgo = P_OnlyOneApplicable) => policySetOnlyOneApplicable[ps, req]
  else NotApplicable    
}

// CONCRETE MODEL


//sigs

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

// subjects

one sig SProfessor extends Subject{}{
  attributes = Role -> Professor
}
one sig SStudent extends Subject{}{
  attributes = Role -> Student
}

// resources

one sig RMarks extends Resource{}{
  attributes = ResourceName ->  MarksFile
}

// actions

one sig ARead extends Action{}{
  attributes = ActionName -> Read
}

one sig AModify extends Action{}{
  attributes = ActionName -> Modify
}

// targets

one sig T0 extends Target {}{
  subjects = SProfessor + SStudent
  resources = RMarks
  actions = ARead + AModify
}

one sig T_Professor_ReadModify extends Target {}{
  subjects = SProfessor
  resources = RMarks
  actions = ARead + AModify
}

one sig T_Student_Read extends Target {}{
  subjects = SStudent
  resources = RMarks
  actions = ARead
}

one sig T_Student_Modify extends Target {}{
  subjects = SStudent
  resources = RMarks
  actions = AModify
}

one sig T_Student_ReadModify extends Target {}{
  subjects = SStudent
  resources = RMarks
  actions = ARead
}

// rules

one sig Rule_Student_ReadModify_Deny extends Rule {}{
  ruleTarget = T_Student_ReadModify
  ruleEffect = Deny
}

one sig Rule_Student_Read_Permit extends Rule {}{
  ruleTarget = T_Student_Read
  ruleEffect = Permit
}

one sig Rule_Professor_ReadModify_Permit extends Rule {}{
  ruleTarget = T_Professor_ReadModify
  ruleEffect = Permit // // inconsistent: Permit
}

one sig Rule_Professor_ReadModify_Deny extends Rule {}{
  ruleTarget = T_Professor_ReadModify
  ruleEffect = Deny // // inconsistent: Deny for the same target
}

// policies


one sig Policy1 extends Policy {}{
  policyTarget = T0
  rules = Rule_Student_Read_Permit /* <fault1> */+ Rule_Professor_ReadModify_Permit /* </fault1> */
  combiningAlgo = DenyOverrides
}

one sig Policy2 extends Policy {}{
  policyTarget = T0
  rules = /* <fault1> */ Rule_Professor_ReadModify_Deny + /* </fault1> */  Rule_Student_Read_Permit
  combiningAlgo = DenyOverrides
}

one sig Policy3 extends Policy {}{
  policyTarget = T0
  rules = Rule_Student_ReadModify_Deny + Rule_Student_Read_Permit
  combiningAlgo = PermitOverrides
}

one sig Policy4 extends Policy {}{
  policyTarget = T0
  rules = Rule_Student_Read_Permit
  combiningAlgo = DenyOverrides
}


// policy set

one sig PS extends PolicySet{}{
  policySetTarget = T0
  combiningAlgo = P_OnlyOneApplicable
  policies = Policy1 + Policy2 + Policy3 + Policy4
}

fact {
  one Request.subject.attributes
}

//==================================
// PREDICATES FOR RUNNING
//==================================


pred InconsistentPolicySet [ps : PolicySet, req : Request, p1: Policy, p2: Policy]{
	ps.combiningAlgo = P_OnlyOneApplicable 
	p1 in ps.policies
	p2 in ps.policies
	p1 != p2
	policyResponse[p1, req] = Permit
	policyResponse[p2, req] = Deny
}

run InconsistentPolicySet


// For testing model:
/*
one sig testReq extends Request{}
{
	subject = SProfessor
	resource = RMarks
	action = ARead
}

pred test{
	policyResponse[Policy1, testReq] = Permit
}
*/

//run test


