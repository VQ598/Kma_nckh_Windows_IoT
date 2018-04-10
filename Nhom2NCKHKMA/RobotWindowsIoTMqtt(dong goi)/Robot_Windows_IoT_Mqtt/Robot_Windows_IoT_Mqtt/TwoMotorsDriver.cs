namespace Robot_Windows_IoT_Mqtt
{
    internal class TwoMotorsDriver
    {
        private Motor _leftMotor;
        private Motor _rightMotor;

        // viết code cho 2 motor
        public TwoMotorsDriver(Motor leftMotor, Motor rightMotor)
        {
            _leftMotor = leftMotor;
            _rightMotor = rightMotor;
        }
        
        public void Stop()
        {
            _leftMotor.Stop1();
            _rightMotor.Stop1();
        }
        public void MoveForward()
        {
            _leftMotor.MoveBackward1();
            _rightMotor.MoveBackward1();
        }
        public void MoveBackward()
        {
            _leftMotor.MoveBackward1();
            _rightMotor.MoveBackward1();
        }
        public void TurnLeft()
        {
            _leftMotor.Stop1();
            _rightMotor.MoveForward1();
        }
        public void TurnRight()
        {
            _leftMotor.MoveForward1();
            _rightMotor.Stop1();
        }
        public void RotateLeft()
        {
            _leftMotor.MoveForward1();
            _rightMotor.MoveBackward1();
        }
        public void RotateRight()
        {
            _leftMotor.MoveBackward1();
            _rightMotor.MoveForward1();
        }
    }
}