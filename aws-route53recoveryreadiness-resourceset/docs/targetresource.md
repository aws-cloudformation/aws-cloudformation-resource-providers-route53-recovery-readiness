# AWS::Route53RecoveryReadiness::ResourceSet TargetResource

The target resource that the Route 53 record points to.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#nlbresource" title="NLBResource">NLBResource</a>" : <i><a href="nlbresource.md">NLBResource</a></i>,
    "<a href="#r53resource" title="R53Resource">R53Resource</a>" : <i><a href="r53resourcerecord.md">R53ResourceRecord</a></i>
}
</pre>

### YAML

<pre>
<a href="#nlbresource" title="NLBResource">NLBResource</a>: <i><a href="nlbresource.md">NLBResource</a></i>
<a href="#r53resource" title="R53Resource">R53Resource</a>: <i><a href="r53resourcerecord.md">R53ResourceRecord</a></i>
</pre>

## Properties

#### NLBResource

The Network Load Balancer resource that a DNS target resource points to.

_Required_: Yes

_Type_: <a href="nlbresource.md">NLBResource</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### R53Resource

The Route 53 resource that a DNS target resource record points to.

_Required_: Yes

_Type_: <a href="r53resourcerecord.md">R53ResourceRecord</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

