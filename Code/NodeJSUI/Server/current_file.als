%2F%2F%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3Cbr%3E%2F%2F%20META%20MODEL%3Cbr%3E%2F%2F%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3Cbr%3E%3Cbr%3Eabstract%20sig%20Value%20%7B%7D%3Cbr%3E%3Cbr%3Eabstract%20sig%20Attribute%20%7B%20values%20%3A%20set%20Value%20%7D%20%3Cbr%3E%3Cbr%3E%2F**Signature%20element%20**%2F%3Cbr%3E%3Cbr%3Eabstract%20sig%20Element%20%7Battributes%20%3AAttribute%20-%3E%20Value%7D%7B%3Cbr%3E%20%20attributes%20in%20values%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Esig%20Subject%2C%20Resource%2C%20Action%20extends%20Element%7B%7D%3Cbr%3E%3Cbr%3E%2F**%20Signature%20Request%20controled%20access**%2F%3Cbr%3E%3Cbr%3Esig%20Request%20%7B%3Cbr%3E%20%20subject%20%3A%20one%20Subject%2C%3Cbr%3E%20%20resource%20%3A%20one%20Resource%2C%3Cbr%3E%20%20action%20%3A%20one%20Action%3Cbr%3E%7D%3Cbr%3E%3Cbr%3E%2F**%20Signature%20Target%20**%2F%3Cbr%3E%3Cbr%3Eabstract%20sig%20Target%20%7B%3Cbr%3E%20%20subjects%20%3A%20set%20Subject%2C%3Cbr%3E%20%20resources%20%3A%20set%20Resource%2C%3Cbr%3E%20%20actions%20%3A%20set%20Action%3Cbr%3E%7D%3Cbr%3E%2F**%20Signature%20effect%20for%20controled%20access%20**%2F%3Cbr%3Eabstract%20sig%20Effect%20%7B%7D%3Cbr%3E%3Cbr%3E%2F**%20possible%20effect%20from%20XACML%20**%2F%3Cbr%3Eone%20sig%20Permit%2C%20Deny%2C%20NotApplicable%2C%20Indeterminate%20extends%20Effect%20%7B%7D%3Cbr%3E%3Cbr%3E%2F**%20Signature%20rule%20for%20controled%20access%20**%2F%3Cbr%3Eabstract%20sig%20Rule%20%7B%3Cbr%3E%20%20ruleTarget%20%3A%20one%20Target%2C%3Cbr%3E%20%20ruleEffect%20%3A%20one%20Effect%3Cbr%3E%7D%3Cbr%3E%3Cbr%3E%2F**%20Signature%20Policy%20controled%20access**%2F%3Cbr%3Eabstract%20sig%20Policy%20%7B%3Cbr%3E%20%20policyTarget%20%3A%20one%20Target%2C%3Cbr%3E%20%20rules%20%3A%20set%20Rule%2C%3Cbr%3E%20%20combiningAlgo%20%3A%20one%20RuleCombiningAlgo%3Cbr%3E%7D%20%3Cbr%3E%3Cbr%3E%2F**%20Signature%20policy%20set%20controled%20access%20**%2F%3Cbr%3Eabstract%20sig%20PolicySet%20%7B%3Cbr%3E%20%20policySetTarget%20%3A%20one%20Target%2C%3Cbr%3E%20%20policies%20%3A%20set%20Policy%2C%3Cbr%3E%20%20policySets%20%3A%20set%20PolicySet%2C%3Cbr%3E%20%20combiningAlgo%20%3A%20one%20PolicyCombiningAlgo%3Cbr%3E%7D%3Cbr%3E%3Cbr%3E%2F**%20Signatures%20of%20Types%20of%20combining%20algorithm%20**%2F%3Cbr%3E%3Cbr%3Eabstract%20sig%20RuleCombiningAlgo%20%7B%7D%3Cbr%3Eabstract%20sig%20PolicyCombiningAlgo%20%7B%7D%3Cbr%3E%3Cbr%3E%2F**%20Types%20of%20combining%20algorithm%20**%2F%3Cbr%3Eone%20sig%20PermitOverrides%2C%20DenyOverrides%20extends%20RuleCombiningAlgo%20%7B%7D%3Cbr%3Eone%20sig%20P_PermitOverrides%2C%20P_DenyOverrides%2C%20P_OnlyOneApplicable%20extends%20PolicyCombiningAlgo%20%7B%7D%3Cbr%3E%3Cbr%3E%2F%2F%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3Cbr%3E%2F%2F%20PREDICATES%3Cbr%3E%2F%2F%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3D%3Cbr%3E%3Cbr%3Epred%20targetMatch%20%5Bt%20%3A%20Target%2C%20q%20%3A%20Request%5D%20%7B%3Cbr%3E%20%20some%20s%3A%20t.subjects%20%7C%20elementMatch%5Bq.subject%2C%20s%5D%3Cbr%3E%20%20some%20r%3A%20t.resources%20%7C%20elementMatch%5Bq.resource%2C%20r%5D%3Cbr%3E%20%20some%20a%3A%20t.actions%20%7C%20elementMatch%5Bq.action%2C%20a%5D%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Epred%20elementMatch%5Be1%3A%20Element%2C%20e2%20%3A%20Element%5D%7B%3Cbr%3E%20%20all%20a2%20%3A%20e2.attributes.Value%3Cbr%3E%20%20%20%20%7Bsome%20a1%20%3A%20e1.attributes.Value%20%3Cbr%3E%20%20%20%20%20%20%20%7B%20a1%3Da2%20and%20a2.(e2.attributes)%20in%20a1.(e1.attributes)%20%7D%7D%3Cbr%3E%7D%3Cbr%3E%3Cbr%3E%3Cbr%3E%2F*%20RULES%20*%2F%3Cbr%3E%3Cbr%3Efun%20ruleResponse%20(r%20%3A%20Rule%2C%20q%20%3A%20Request)%20%3A%20Effect%20%7B%3Cbr%3E%20%20targetMatch%5Br.ruleTarget%2C%20q%5D%20%3D%3E%20r.ruleEffect%3Cbr%3E%20%20else%20NotApplicable%3Cbr%3E%7D%3Cbr%3E%3Cbr%3E%2F*%20POLICIES%20*%2F%3Cbr%3E%3Cbr%3Epred%20policyExistsRuleDeny%5Bp%20%3A%20Policy%2C%20q%20%3A%20Request%5D%20%7B%3Cbr%3E%20%20some%20r%20%3A%20p.rules%20%7C%20ruleResponse%5Br%2C%20q%5D%20%3D%20Deny%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Epred%20policyExistsRulePermit%5Bp%20%3A%20Policy%2C%20q%20%3A%20Request%5D%20%7B%3Cbr%3E%20%20some%20r%20%3A%20p.rules%20%7C%20ruleResponse%5Br%2C%20q%5D%20%3D%20Permit%3Cbr%3E%7D%3Cbr%3E%3Cbr%3E%3Cbr%3E%2F%2F%20policies%20have%20only%202%20rule%20combining%20algorithms%3A%20PermitOverrides%20and%20DenyOverrides%3Cbr%3E%2F%2F%20first-applicable%20is%20not%20supported%20since%20we%20don't%20introduce%20order%3Cbr%3Efun%20policyPermitOverrides%20(%20p%20%3A%20Policy%2C%20q%20%3A%20Request)%20%3A%20Effect%20%7B%3Cbr%3E%20%20policyExistsRulePermit%5Bp%2Cq%5D%20%3D%3E%20Permit%3Cbr%3E%20%20else%20policyExistsRuleDeny%5Bp%2Cq%5D%20%3D%3E%20Deny%3Cbr%3E%20%20else%20NotApplicable%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Efun%20policyDenyOverrides%20(%20p%20%3A%20Policy%2C%20req%20%3A%20Request)%20%3A%20Effect%20%7B%3Cbr%3E%20%20policyExistsRuleDeny%5Bp%2Creq%5D%3D%3E%20Deny%3Cbr%3E%20%20else%20policyExistsRulePermit%5Bp%2Creq%5D%20%3D%3EPermit%3Cbr%3E%20%20else%20NotApplicable%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Efun%20policyResponse%20(p%20%3A%20Policy%2C%20req%20%3A%20Request)%20%3A%20Effect%20%7B%3Cbr%3E%20%20(p.combiningAlgo%20%3D%20PermitOverrides)%20%3D%3E%20policyPermitOverrides%5Bp%2C%20req%5D%3Cbr%3E%20%20else%20(p.combiningAlgo%20%3D%20DenyOverrides)%20%3D%3E%20policyDenyOverrides%5Bp%2C%20req%5D%3Cbr%3E%20%20else%20NotApplicable%20%20%20%20%3Cbr%3E%7D%3Cbr%3E%3Cbr%3E%2F*%20POLICY%20SETS%20*%2F%3Cbr%3E%3Cbr%3Epred%20policySetExistsPolicyDeny%5Bps%20%3A%20PolicySet%2C%20q%20%3A%20Request%5D%20%7B%3Cbr%3E%20%20some%20p%20%3A%20ps.policies%20%7C%20policyResponse%5Bp%2C%20q%5D%20%3D%20Deny%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Epred%20policySetExistsPolicyPermit%5Bps%20%3A%20PolicySet%2C%20q%20%3A%20Request%5D%20%7B%3Cbr%3E%20%20some%20p%20%3A%20ps.policies%20%7C%20policyResponse%5Bp%2C%20q%5D%20%3D%20Permit%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Efun%20policySetOnlyOneApplicable(%20ps%20%3A%20PolicySet%2C%20req%20%3A%20Request)%20%3A%20Effect%20%7B%3Cbr%3E%20%20(policySetExistsPolicyDeny%5Bps%2Creq%5D%20%26%26%20policySetExistsPolicyPermit%5Bps%2Creq%5D)%20%3D%3E%20Indeterminate%3Cbr%3E%20%20else%20policySetExistsPolicyPermit%5Bps%2Creq%5D%20%3D%3EPermit%3Cbr%3E%20%20else%20policySetExistsPolicyDeny%5Bps%2Creq%5D%20%3D%3EDeny%3Cbr%3E%20%20else%20NotApplicable%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Efun%20policySetResponse%20(ps%20%3A%20PolicySet%2C%20req%20%3A%20Request)%20%3A%20Effect%20%7B%3Cbr%3E%2F%2F%20we%20support%20only%20OnlyOneApplicable%3Cbr%3E%20%20(ps.combiningAlgo%20%3D%20P_OnlyOneApplicable)%20%3D%3E%20policySetOnlyOneApplicable%5Bps%2C%20req%5D%3Cbr%3E%20%20else%20NotApplicable%20%20%20%20%3Cbr%3E%7D%3Cbr%3E%3Cbr%3E%3Cbr%3Eone%20sig%20ActionName%20extends%20Attribute%20%7B%7D%7B%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20Role%20extends%20Attribute%20%7B%7D%7B%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20ResourceName%20extends%20Attribute%20%7B%7D%7B%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Efact%7B%3Cbr%3E%20%20Subject.attributes.Value%20%3D%20Role%3Cbr%3E%20%20Resource.attributes.Value%20%3D%20ResourceName%3Cbr%3E%20%20Action.attributes.Value%20%3D%20ActionName%3Cbr%3E%7D%3Cbr%3E%3Cbr%3E%2F%2F%20CONCRETE%20MODEL%3Cbr%3E%3Cbr%3E%3Cbr%3Eone%20sig%20Student%2C%20Professor%20extends%20Value%20%7B%7D%3Cbr%3Eone%20sig%20Marks%20extends%20Value%20%7B%7D%3Cbr%3Eone%20sig%20Read%2C%20Modify%20extends%20Value%20%7B%7D%3Cbr%3Efact%7B%20%3Cbr%3E%20values%20%3D%20%3Cbr%3E(ActionName%20-%3E%20Read)%3Cbr%3E%20%2B(Role%20-%3E%20Professor)%3Cbr%3E%20%2B(ActionName%20-%3E%20Modify)%3Cbr%3E%20%2B(Role%20-%3E%20Student)%3Cbr%3E%20%2B(ResourceName%20-%3E%20Marks)%7D%3Cbr%3E%3Cbr%3Eone%20sig%20SStudent%20extends%20Subject%7B%7D%7B%3Cbr%3E%20attributes%20%3D%20Role%20-%3E%20Student%20%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20SProfessor%20extends%20Subject%7B%7D%7B%3Cbr%3E%20attributes%20%3D%20Role%20-%3E%20Professor%20%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20RMarks%20extends%20Resource%7B%7D%7B%3Cbr%3E%20attributes%20%3D%20ResourceName%20-%3E%20Marks%20%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20ARead%20extends%20Action%7B%7D%7B%3Cbr%3E%20attributes%20%3D%20ActionName%20-%3E%20Read%20%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20AModify%20extends%20Action%7B%7D%7B%3Cbr%3E%20attributes%20%3D%20ActionName%20-%3E%20Modify%20%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20T0%20extends%20Target%20%7B%7D%7B%3Cbr%3E%20subjects%20%3D%20SStudent%20%2B%20SProfessor%20%3Cbr%3E%20resources%20%3D%20RMarks%20%3Cbr%3E%20actions%20%3D%20ARead%20%2B%20AModify%20%7D%3Cbr%3E%3Cbr%3Eone%20sig%20Policy1%20extends%20Policy%20%7B%7D%7B%3Cbr%3EpolicyTarget%20%3D%20T0%3Cbr%3Erules%20%3D%20Policy1_Rule_Student_Read_Marks_Permit%20%2B%20Policy1_Rule_Professor_ReadModify_Marks_Permit%3Cbr%3EcombiningAlgo%20%3D%20DenyOverrides%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20Policy1_Rule_Professor_ReadModify_Marks_Permit%20extends%20Rule%20%7B%7D%7B%3Cbr%3EruleTarget%20%3D%20Policy1_Target_Professor_ReadModify_Marks_Permit%3Cbr%3EruleEffect%20%3D%20Permit%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20Policy1_Target_Professor_ReadModify_Marks_Permit%20extends%20Target%20%7B%7D%7B%3Cbr%3Esubjects%20%3D%20SProfessor%3Cbr%3Eresources%20%3D%20RMarks%3Cbr%3Eactions%20%3D%20ARead%20%2B%20AModify%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20Policy1_Rule_Student_Read_Marks_Permit%20extends%20Rule%20%7B%7D%7B%3Cbr%3EruleTarget%20%3D%20Policy1_Target_Student_Read_Marks_Permit%3Cbr%3EruleEffect%20%3D%20Permit%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20Policy1_Target_Student_Read_Marks_Permit%20extends%20Target%20%7B%7D%7B%3Cbr%3Esubjects%20%3D%20SStudent%3Cbr%3Eresources%20%3D%20RMarks%3Cbr%3Eactions%20%3D%20ARead%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20Policy2%20extends%20Policy%20%7B%7D%7B%3Cbr%3EpolicyTarget%20%3D%20T0%3Cbr%3Erules%20%3D%20Policy2_Rule_Student_Read_Marks_Permit%20%2B%20Policy2_Rule_Student_ReadModify_Marks_Deny%3Cbr%3EcombiningAlgo%20%3D%20PermitOverrides%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20Policy2_Rule_Student_Read_Marks_Permit%20extends%20Rule%20%7B%7D%7B%3Cbr%3EruleTarget%20%3D%20Policy2_Target_Student_Read_Marks_Permit%3Cbr%3EruleEffect%20%3D%20Permit%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20Policy2_Target_Student_Read_Marks_Permit%20extends%20Target%20%7B%7D%7B%3Cbr%3Esubjects%20%3D%20SStudent%3Cbr%3Eresources%20%3D%20RMarks%3Cbr%3Eactions%20%3D%20ARead%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20Policy2_Rule_Student_ReadModify_Marks_Deny%20extends%20Rule%20%7B%7D%7B%3Cbr%3EruleTarget%20%3D%20Policy2_Target_Student_ReadModify_Marks_Deny%3Cbr%3EruleEffect%20%3D%20Deny%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20Policy2_Target_Student_ReadModify_Marks_Deny%20extends%20Target%20%7B%7D%7B%3Cbr%3Esubjects%20%3D%20SStudent%3Cbr%3Eresources%20%3D%20RMarks%3Cbr%3Eactions%20%3D%20ARead%20%2B%20AModify%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20Policy3%20extends%20Policy%20%7B%7D%7B%3Cbr%3EpolicyTarget%20%3D%20T0%3Cbr%3Erules%20%3D%20Policy3_Rule_Professor_Modify_Marks_Permit%20%2B%20Policy3_Rule_Professor_Read_Marks_Permit%20%2B%20Policy3_Rule_Professor_ReadModify_Marks_Deny%20%2B%20Policy3_Rule_Student_Read_Marks_Permit%3Cbr%3EcombiningAlgo%20%3D%20DenyOverrides%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20Policy3_Rule_Professor_ReadModify_Marks_Deny%20extends%20Rule%20%7B%7D%7B%3Cbr%3EruleTarget%20%3D%20Policy3_Target_Professor_ReadModify_Marks_Deny%3Cbr%3EruleEffect%20%3D%20Deny%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20Policy3_Target_Professor_ReadModify_Marks_Deny%20extends%20Target%20%7B%7D%7B%3Cbr%3Esubjects%20%3D%20SProfessor%3Cbr%3Eresources%20%3D%20RMarks%3Cbr%3Eactions%20%3D%20ARead%20%2B%20AModify%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20Policy3_Rule_Professor_Modify_Marks_Permit%20extends%20Rule%20%7B%7D%7B%3Cbr%3EruleTarget%20%3D%20Policy3_Target_Professor_Modify_Marks_Permit%3Cbr%3EruleEffect%20%3D%20Permit%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20Policy3_Target_Professor_Modify_Marks_Permit%20extends%20Target%20%7B%7D%7B%3Cbr%3Esubjects%20%3D%20SProfessor%3Cbr%3Eresources%20%3D%20RMarks%3Cbr%3Eactions%20%3D%20AModify%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20Policy3_Rule_Professor_Read_Marks_Permit%20extends%20Rule%20%7B%7D%7B%3Cbr%3EruleTarget%20%3D%20Policy3_Target_Professor_Read_Marks_Permit%3Cbr%3EruleEffect%20%3D%20Permit%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20Policy3_Target_Professor_Read_Marks_Permit%20extends%20Target%20%7B%7D%7B%3Cbr%3Esubjects%20%3D%20SProfessor%3Cbr%3Eresources%20%3D%20RMarks%3Cbr%3Eactions%20%3D%20ARead%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20Policy3_Rule_Student_Read_Marks_Permit%20extends%20Rule%20%7B%7D%7B%3Cbr%3EruleTarget%20%3D%20Policy3_Target_Student_Read_Marks_Permit%3Cbr%3EruleEffect%20%3D%20Permit%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20Policy3_Target_Student_Read_Marks_Permit%20extends%20Target%20%7B%7D%7B%3Cbr%3Esubjects%20%3D%20SStudent%3Cbr%3Eresources%20%3D%20RMarks%3Cbr%3Eactions%20%3D%20ARead%3Cbr%3E%7D%3Cbr%3E%3Cbr%3Eone%20sig%20PS%20extends%20PolicySet%7B%7D%7B%3Cbr%3EpolicySetTarget%20%3D%20T0%3Cbr%3EcombiningAlgo%20%3D%20P_OnlyOneApplicable%3Cbr%3Epolicies%20%3D%20Policy1%20%2B%20Policy2%20%2B%20Policy3%3Cbr%3E%7D%3Cbr%3E