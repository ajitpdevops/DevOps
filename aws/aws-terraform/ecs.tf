resource "aws_ecs_cluster" "sas_ecs_cluster" {
  name = "${var.environment}-ecs-cluster"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }
}

resource "aws_autoscaling_group" "sas_asg" {
    name = "${var.environment}-ecs-asg"
    
    desired_capacity = 1
    max_size         = 1
    min_size         = 1
    
    launch_configuration = aws_launch_configuration.sas_launch_config.name
    vpc_zone_identifier = [aws_subnet.sas_subnet.id]

    tag {
        key                 = "Name"
        value               = "${var.environment}-ecs-asg"
        propagate_at_launch = true
    }
  
}