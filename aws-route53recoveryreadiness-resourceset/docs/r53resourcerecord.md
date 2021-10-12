# AWS::Route53RecoveryReadiness::ResourceSet R53ResourceRecord

The Route 53 resource that a DNS target resource record points to.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#domainname" title="DomainName">DomainName</a>" : <i>String</i>,
    "<a href="#recordsetid" title="RecordSetId">RecordSetId</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#domainname" title="DomainName">DomainName</a>: <i>String</i>
<a href="#recordsetid" title="RecordSetId">RecordSetId</a>: <i>String</i>
</pre>

## Properties

#### DomainName

The DNS target domain name.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### RecordSetId

The Resource Record set id.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

