//===============================================
// META MODEL
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

pred policyExistsRuleDeny[p : Policy, q : Request] {
  some r : p.rules | ruleResponse[r, q] = Deny
}

pred policyExistsRulePermit[p : Policy, q : Request] {
  some r : p.rules | ruleResponse[r, q] = Permit
}


// policies have only 2 rule combining algorithms: PermitOverrides and DenyOverrides
// first-applicable is not supported since we don't introduce order
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

fun policyResponse (p : Policy, req : Request) : Effect {
  (p.combiningAlgo = PermitOverrides) => policyPermitOverrides[p, req]
  else (p.combiningAlgo = DenyOverrides) => policyDenyOverrides[p, req]
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


one sig ActionName extends Attribute {}{
}

one sig Role extends Attribute {}{
}

one sig ResourceName extends Attribute {}{
}

fact{
  Subject.attributes.Value = Role
  Resource.attributes.Value = ResourceName
  Action.attributes.Value = ActionName
}

// CONCRETE MODEL


one sig Student, Professor extends Value {}
one sig Marks extends Value {}
one sig Read, Modify extends Value {}
fact{ 
 values = 
(ActionName -> Read)
 +(Role -> Professor)
 +(ActionName -> Modify)
 +(Role -> Student)
 +(ResourceName -> Marks)}

one sig SStudent extends Subject{}{
 attributes = Role -> Student 
}

one sig SProfessor extends Subject{}{
 attributes = Role -> Professor 
}

one sig RMarks extends Resource{}{
 attributes = ResourceName -> Marks 
}

one sig ARead extends Action{}{
 attributes = ActionName -> Read 
}

one sig AModify extends Action{}{
 attributes = ActionName -> Modify 
}

one sig T0 extends Target {}{
 subjects = SStudent + SProfessor 
 resources = RMarks 
 actions = ARead + AModify }

one sig Policy1 extends Policy {}{
policyTarget = T0
rules = Policy1_Rule_Student_Read_Marks_Permit + Policy1_Rule_Professor_ReadModify_Marks_Permit
combiningAlgo = DenyOverrides
}

one sig Policy1_Rule_Student_Read_Marks_Permit extends Rule {}{
ruleTarget = Policy1_Target_Student_Read_Marks_Permit
ruleEffect = Permit
}

one sig Policy1_Target_Student_Read_Marks_Permit extends Target {}{
subjects = SStudent
resources = RMarks
actions = ARead
}

one sig Policy1_Rule_Professor_ReadModify_Marks_Permit extends Rule {}{
ruleTarget = Policy1_Target_Professor_ReadModify_Marks_Permit
ruleEffect = Permit
}

one sig Policy1_Target_Professor_ReadModify_Marks_Permit extends Target {}{
subjects = SProfessor
resources = RMarks
actions = ARead + AModify
}

one sig Policy2 extends Policy {}{
policyTarget = T0
rules = Policy2_Rule_Professor_ReadModify_Marks_Deny + Policy2_Rule_Student_Read_Marks_Permit
combiningAlgo = PermitOverrides
}

one sig Policy2_Rule_Professor_ReadModify_Marks_Deny extends Rule {}{
ruleTarget = Policy2_Target_Professor_ReadModify_Marks_Deny
ruleEffect = Deny
}

one sig Policy2_Target_Professor_ReadModify_Marks_Deny extends Target {}{
subjects = SProfessor
resources = RMarks
actions = ARead + AModify
}

one sig Policy2_Rule_Student_Read_Marks_Permit extends Rule {}{
ruleTarget = Policy2_Target_Student_Read_Marks_Permit
ruleEffect = Permit
}

one sig Policy2_Target_Student_Read_Marks_Permit extends Target {}{
subjects = SStudent
resources = RMarks
actions = ARead
}

one sig Policy3 extends Policy {}{
policyTarget = T0
rules = Policy3_Rule_Professor_Modify_Marks_Permit + Policy3_Rule_Professor_Read_Marks_Permit + Policy3_Rule_Professor_ReadModify_Marks_Deny + Policy3_Rule_Student_Read_Marks_Permit
combiningAlgo = DenyOverrides
}

one sig Policy3_Rule_Professor_Read_Marks_Permit extends Rule {}{
ruleTarget = Policy3_Target_Professor_Read_Marks_Permit
ruleEffect = Permit
}

one sig Policy3_Target_Professor_Read_Marks_Permit extends Target {}{
subjects = SProfessor
resources = RMarks
actions = ARead
}

one sig Policy3_Rule_Student_Read_Marks_Permit extends Rule {}{
ruleTarget = Policy3_Target_Student_Read_Marks_Permit
ruleEffect = Permit
}

one sig Policy3_Target_Student_Read_Marks_Permit extends Target {}{
subjects = SStudent
resources = RMarks
actions = ARead
}

one sig Policy3_Rule_Professor_ReadModify_Marks_Deny extends Rule {}{
ruleTarget = Policy3_Target_Professor_ReadModify_Marks_Deny
ruleEffect = Deny
}

one sig Policy3_Target_Professor_ReadModify_Marks_Deny extends Target {}{
subjects = SProfessor
resources = RMarks
actions = ARead + AModify
}

one sig Policy3_Rule_Professor_Modify_Marks_Permit extends Rule {}{
ruleTarget = Policy3_Target_Professor_Modify_Marks_Permit
ruleEffect = Permit
}

one sig Policy3_Target_Professor_Modify_Marks_Permit extends Target {}{
subjects = SProfessor
resources = RMarks
actions = AModify
}

one sig PS extends PolicySet{}{
policySetTarget = T0
combiningAlgo = P_OnlyOneApplicable
policies = Policy1 + Policy2 + Policy3
}


//==================================
// PREDICATES FOR RUNNING
//==================================


pred InconsistentPolicySet [ps : PolicySet, req : Request, p1: Policy, p2: Policy, r1: Rule, r2: Rule]{
	ps.combiningAlgo = P_OnlyOneApplicable 
	p1 in ps.policies
	p2 in ps.policies
	p1 != p2
	r1 in p1.rules
	r2 in p2.rules
	policyResponse[p1, req] = Permit
	(
		p1.combiningAlgo = DenyOverrides and
		(no r1':Rule | r1' in p1.rules and ruleResponse[r1', req] = Deny)
		and ruleResponse[r1, req] = Permit
	)
	or
	(
		p1.combiningAlgo = PermitOverrides
		and ruleResponse[r1, req] = Permit
	)
	policyResponse[p2, req] = Deny
	(
		p2.combiningAlgo = PermitOverrides and
		(no r2':Rule | r2' in p2.rules and ruleResponse[r2', req] = Permit)
		and ruleResponse[r2, req] = Deny
	)
	or
	(
		p2.combiningAlgo = DenyOverrides
		and ruleResponse[r2, req] = Deny
	)

}

run InconsistentPolicySet
