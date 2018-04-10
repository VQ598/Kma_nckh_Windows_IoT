using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;
using uPLibrary.Networking.M2Mqtt;
using uPLibrary.Networking.M2Mqtt.Messages;
using System.Text;
using System.Diagnostics;
using Windows.Devices.Gpio;
using System.Threading.Tasks;
using System.Threading;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=402352&clcid=0x409

namespace Robot_Windows_IoT_Mqtt
{

    public sealed partial class MainPage : Page
    {
       
        TwoMotorsDriver driver = new TwoMotorsDriver(new Motor(27, 22), new Motor(5, 6));


        private GpioPin pinRelayIn1;
        private GpioPin pinRelayIn2;
        private GpioPin pinLED_G;
        private GpioPin pinLED_R;
        private GpioPin pinLED_B;
        short msgID;
        public MainPage()
        {
            this.InitializeComponent();
            MqttConnect();
            khoitaoPin();
            // show led lúc khởi động 
            Led_Khoi_Dong();


        }
        
        void khoitaoPin()
        { 
            GpioController myGpio = GpioController.GetDefault();
            pinLED_G = myGpio.OpenPin(24);
            pinLED_G.Write(GpioPinValue.Low);
            pinLED_G.SetDriveMode(GpioPinDriveMode.Output);

            pinLED_R = myGpio.OpenPin(18);
            pinLED_R.Write(GpioPinValue.Low);
            pinLED_R.SetDriveMode(GpioPinDriveMode.Output);

            pinLED_B = myGpio.OpenPin(23);
            pinLED_B.Write(GpioPinValue.Low);
            pinLED_B.SetDriveMode(GpioPinDriveMode.Output);

            pinRelayIn1 = myGpio.OpenPin(4);
            pinRelayIn1.Write(GpioPinValue.Low);
            pinRelayIn1.SetDriveMode(GpioPinDriveMode.Output);

            pinRelayIn2 = myGpio.OpenPin(17);
            pinRelayIn2.Write(GpioPinValue.Low);
            pinRelayIn2.SetDriveMode(GpioPinDriveMode.Output);

        }
        async void Led_Khoi_Dong()
        {
            pinLED_R.Write(GpioPinValue.Low);
            pinLED_B.Write(GpioPinValue.Low);
            pinLED_G.Write(GpioPinValue.High);
            await Task.Delay(TimeSpan.FromMilliseconds(250));
            pinLED_G.Write(GpioPinValue.Low);
            pinLED_R.Write(GpioPinValue.Low);
            pinLED_B.Write(GpioPinValue.High);
            await Task.Delay(TimeSpan.FromMilliseconds(250));
            pinLED_G.Write(GpioPinValue.Low);
            pinLED_B.Write(GpioPinValue.Low);
            pinLED_R.Write(GpioPinValue.High);

        }


        private void MqttConnect()
        {
            MqttClient client;
            string username = "eqzcaosj";
            string password = "NFmNNSuRgQS-";
            string host = "m14.cloudmqtt.com";
            int port = 14953;
            ushort msgId;
            client = new MqttClient(host, port, false, MqttSslProtocols.None);
            byte code = client.Connect(Guid.NewGuid().ToString(), username, password);
            client.Subscribe(new string[] { "Robot2" }, new byte[] { 0 });
            msgId = client.Publish("Robot2", // topic
                                            Encoding.UTF8.GetBytes("Mqtt đã kết nối với Raspberry thành công !"), // message body
                                            MqttMsgBase.QOS_LEVEL_EXACTLY_ONCE, // QoS level
                                            false); // retained 
            client.MqttMsgPublishReceived += client_MqttMsgPublishReceived;
            
        }



        private void client_MqttMsgPublishReceived(object sender, MqttMsgPublishEventArgs e)
        {

            Debug.WriteLine("Recevied = " + Encoding.UTF8.GetString(e.Message) + " on topic" + e.Topic);
            switch (Encoding.UTF8.GetString(e.Message))
            {
                case "Mqtt đã kết nối với Raspberry thành công !":
                    {
                        pinLED_G.Write(GpioPinValue.High);
                        pinLED_R.Write(GpioPinValue.Low);
                        pinLED_B.Write(GpioPinValue.Low);
                        Debug.WriteLine("Den xanh bat ");
                        // led kiểm tra , nếu kết nối thành công sẽ hiện led xanh ! 
                    }
                    break;
                case "move forward":
                    {
                        driver.MoveForward();
                        Debug.WriteLine("Tien len");
                    }
                    break;
                case "move backward": driver.MoveBackward(); break;
                case "turn left": driver.TurnLeft(); break;
                case "turn right": driver.TurnRight(); break;
                case "rotate left": driver.RotateLeft(); break;
                case "rotate right": driver.RotateRight(); break;
                case "stop": driver.Stop(); break;
                // Thiet bi dien 
                case "enable lamp 1": pinRelayIn1.Write(GpioPinValue.High); break;
                case "disable lamp 1": pinRelayIn1.Write(GpioPinValue.Low); break;
                case "enable lamp 2": pinRelayIn2.Write(GpioPinValue.High); break;
                case "disable lamp 2": pinRelayIn2.Write(GpioPinValue.Low); break;
                case "enable all lamp":
                    {
                        pinRelayIn1.Write(GpioPinValue.High);
                        pinRelayIn2.Write(GpioPinValue.High);
                    }; break;
                case "disable all lamp":
                    {
                        pinRelayIn1.Write(GpioPinValue.Low);
                        pinRelayIn2.Write(GpioPinValue.Low);
                    }; break;
            }

        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            base.OnNavigatedTo(e);
            Window.Current.CoreWindow.KeyDown += HandleKeyDown;
        }

        protected override void OnNavigatedFrom(NavigationEventArgs e)
        {
            base.OnNavigatedFrom(e);
            Window.Current.CoreWindow.KeyDown -= HandleKeyDown;
        }

        private void HandleKeyDown(Windows.UI.Core.CoreWindow sender, Windows.UI.Core.KeyEventArgs args)
        {
            if (args.VirtualKey == Windows.System.VirtualKey.Right)
                driver.TurnRight();
            else if (args.VirtualKey == Windows.System.VirtualKey.Left)
                driver.TurnLeft();
            else if (args.VirtualKey == Windows.System.VirtualKey.Up)
                driver.MoveForward();
            else if (args.VirtualKey == Windows.System.VirtualKey.Down)
                driver.MoveBackward();
            if (args.VirtualKey == Windows.System.VirtualKey.Escape)
                driver.Stop();
            if (args.VirtualKey == Windows.System.VirtualKey.A)
                driver.RotateRight();
            if (args.VirtualKey == Windows.System.VirtualKey.Z)
                driver.RotateLeft();

        }
    }
}
