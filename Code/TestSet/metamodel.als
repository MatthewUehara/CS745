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
