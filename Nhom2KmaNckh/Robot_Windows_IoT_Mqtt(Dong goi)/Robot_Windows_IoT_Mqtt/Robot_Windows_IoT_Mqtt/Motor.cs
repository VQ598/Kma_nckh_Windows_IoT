using Windows.Devices.Gpio;

namespace Robot_Windows_IoT_Mqtt
{
    public class Motor
    {
        // viết code cho 1 motor
        private readonly GpioPin _motorGpioPinA;
        private readonly GpioPin _motorGpioPinB;
        public Motor(int gpioPinIn1, int gpioPinIn2)
        {
            var gpio = GpioController.GetDefault();
            _motorGpioPinA = gpio.OpenPin(gpioPinIn1);
            _motorGpioPinB = gpio.OpenPin(gpioPinIn2);
            _motorGpioPinA.Write(GpioPinValue.Low);
            _motorGpioPinB.Write(GpioPinValue.Low);
            _motorGpioPinA.SetDriveMode(GpioPinDriveMode.Output);
            _motorGpioPinB.SetDriveMode(GpioPinDriveMode.Output);


        }
        public void MoveForward1()
        {
            _motorGpioPinA.Write(GpioPinValue.Low);
            _motorGpioPinB.Write(GpioPinValue.High);
        }
        public void MoveBackward1()
        {
            _motorGpioPinA.Write(GpioPinValue.High);
            _motorGpioPinB.Write(GpioPinValue.Low);
        }
        public void Stop1()
        {
            _motorGpioPinA.Write(GpioPinValue.Low);
            _motorGpioPinB.Write(GpioPinValue.Low);
        }
    }
}