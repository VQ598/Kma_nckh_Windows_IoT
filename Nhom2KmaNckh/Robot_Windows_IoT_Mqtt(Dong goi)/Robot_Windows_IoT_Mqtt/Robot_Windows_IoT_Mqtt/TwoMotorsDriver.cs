using System;
using System.Threading.Tasks;

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
            _leftMotor.MoveForward1();
            _rightMotor.MoveForward1();
        }
        public void MoveBackward()
        {
            _leftMotor.MoveBackward1();
            _rightMotor.MoveBackward1();
        }
        public async Task TurnLeftAsync()
        {
            _leftMotor.Stop1();
            _rightMotor.MoveForward1();
            await Task.Delay(TimeSpan.FromMilliseconds(250));
            MoveForward();
        }
        public async Task TurnRightAsync()
        {
            _leftMotor.MoveForward1();
            _rightMotor.Stop1();
            await Task.Delay(TimeSpan.FromMilliseconds(250));
            MoveForward();
        }
        public void RotateLeft()
        {
            _leftMotor.MoveForward1();
            _rightMotor.MoveBackward1();
        }
        public async System.Threading.Tasks.Task RotateRightAsync()
        {
            _leftMotor.MoveBackward1();
            _rightMotor.MoveForward1();
            //await Task.Delay(TimeSpan.FromSeconds(1));
            //_leftMotor.Stop1();
            //_rightMotor.Stop1();
        }

        #region driver for Voice
        public async Task MoveForwardAsyncV()
        {
            _leftMotor.MoveForward1();
            _rightMotor.MoveForward1();
            await Task.Delay(TimeSpan.FromSeconds(1));
            Stop();
        }
        public async Task MoveBackwardAsyncV()
        {
            _leftMotor.MoveBackward1();
            _rightMotor.MoveBackward1();
            await Task.Delay(TimeSpan.FromSeconds(1));
            Stop();
        }
        public async Task TurnLeftAsyncV()
        {
            _leftMotor.Stop1();
            _rightMotor.MoveForward1();
            await Task.Delay(TimeSpan.FromSeconds(1));
            Stop();
        }
        public async Task TurnRightAsyncV()
        {
            _leftMotor.MoveForward1();
            _rightMotor.Stop1();
            await Task.Delay(TimeSpan.FromSeconds(1));
            Stop();
        }
        public async Task RotateLeftAsyncV()
        {
            _leftMotor.MoveForward1();
            _rightMotor.MoveBackward1();
            await Task.Delay(TimeSpan.FromSeconds(1));
            Stop();
        }
        public async System.Threading.Tasks.Task RotateRightAsyncV()
        {
            _leftMotor.MoveBackward1();
            _rightMotor.MoveForward1();
            await Task.Delay(TimeSpan.FromSeconds(1));
            Stop();
        }
        #endregion

    }
}