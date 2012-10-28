open util/integer
pred show {}
run  show for 1 but 2 c12_WaitingLine, 5 c1_Person, 5 c2_name

abstract sig c1_Person
{ r_c2_name : one c2_name }

sig c2_name
{ ref : one Int }
{ one @r_c2_name.this }

one sig c3_Alice extends c1_Person
{}
{ (this.@r_c2_name.@ref) = 0 }

one sig c7_Bob extends c1_Person
{}
{ (this.@r_c2_name.@ref) = 1 }

fact { 3 <= #c11_BobsTeamMember and #c11_BobsTeamMember <= 3 }
sig c11_BobsTeamMember extends c1_Person
{}

fact { 2 <= #c12_WaitingLine and #c12_WaitingLine <= 7 }
sig c12_WaitingLine
{ ref : one c1_Person }

fact { all disj x, y : c12_WaitingLine | (x.@ref) != (y.@ref) }
fact { ((c3_Alice in (c12_WaitingLine.@ref)) && (c7_Bob in (c12_WaitingLine.@ref))) && (c11_BobsTeamMember in (c12_WaitingLine.@ref)) }
