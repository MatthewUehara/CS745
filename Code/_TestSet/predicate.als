
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
