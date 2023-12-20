# Multi Account Based Architect 
It gives highest amount of resouces isolation and security. When an organization has multiple accounts, it can use AWS Organizations to create groups of accounts and then apply policies to those groups. This allows you to manage permissions for those groups centrally.
- Identity account architecture : It is recommended to manage all users and groups in a single account. This account is called identity account. 
- Logging account architecture : It is recommended to manage all logs in a single account. This account is called logging account.
- Publishing account architecture : It is recommended to manage all public resources in a single account. This account is called publishing account.
- Billing structure : It is recommended to manage all billing in a single account. This account is called billing account.

## Identity Account Architecture 
Identity account  

### Creating cross account IAM Role 
Let us consider identity account as Account-A 
Let us also consider that we have 2 other accounts Account-B and Account-C.
In order enable to the Cross Account Access, we need to do following things - 
- Create a User Account in Account-A
- Create a cross account role in Account-B and Account-C, you can add necessary policies to the role. 
- Attach the role assume policy to the User Account in Account-A, here is the example policy 
```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "AllowCrossAccountAssumeRole",
            "Effect": "Allow",
            "Action": "sts:AssumeRole",
            "Resource": "arn:aws:iam::<Account-B>:role/<Role-Name>"
        }
    ]
}
```

This is a common approach but still require a lot of administration overhead. Because you will have to create an IAM user for each user in the identity account. Then you will have to create cross account role for each account. And then ensure the role policies are updated for each role.
Finally allow each user to switch to the approriate account by attaching the role assume policy to the user.

Homework: What is better approach to solve this management overhead by using an identity provider and AWS SSO or AD Connector?

## AWS Organizations
AWS Organizations is an account management service that enables you to consolidate multiple AWS accounts into an organization that you create and centrally manage. AWS Organizations includes account management and consolidated billing capabilities that enable you to better meet the budgetary, security, and compliance needs of your business. As an administrator of an organization, you can create accounts in your organization and invite existing accounts to join the organization. You can organize those accounts into groups and attach policy-based controls. AWS Organizations offers API access to all of its functionality so that you can automate to scale.

1. Consolidated Billing - Consolidated billing enables you to see a combined view of AWS charges incurred by all accounts, as well as obtain a detailed cost report for each of the individual AWS accounts associated with your master account. Consolidated billing is offered at no additional charge.

2. Service Control Policies - Service control policies (SCPs) are a type of organization policy that you can use to manage permissions in your organization. SCPs offer central control over the maximum available permissions for all accounts in your organization. SCPs are available only in AWS Organizations.

3. Tagging - You can use tags to organize your resources. Tags enable you to categorize your AWS resources in different ways, for example, by purpose, owner, or environment. This is useful when you have many resources of the same type — you can quickly identify a specific resource based on the tags you've assigned to it. Each tag consists of a key and an optional value, both of which you define. You can edit the tag values at any time. For each resource, you can assign up to 50 tags.

Homework? 
- What are some of the most common Service Control Policies (SCPs) that you can apply to your organization?
- What are some of the most common tags that you can apply to your organization?
- What we leverage Account Vending Machine (AVM) to create new accounts in AWS Organizations?


## Organizational Units (OU)
An organizational unit (OU) is a container for accounts within a root of an organization. You can use OUs to group accounts to more easily organize your accounts by region, business unit, or application. You can also use OUs to delegate control over those accounts to your users and groups. When you create an OU, you can add accounts to it and then apply policies to the OU that automatically apply to all of the accounts in it. You can also move accounts between OUs as your needs change.

## IAM Permission Boundaries
An IAM permissions boundary is an advanced feature for using a managed policy to set the maximum permissions that an identity-based policy can grant to an IAM entity. 
When you assign a permissions boundary to an entity, the permissions boundary defines the maximum permissions that the entity's identity-based policies can grant to the entity.


