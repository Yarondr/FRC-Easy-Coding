print("Loading...")
import sys

from PyQt5.QtWidgets import QApplication

from GUI.code_screen import CodeScreen


def run_gui():
    print("Starting program...")
    app = QApplication(sys.argv)
    screen = CodeScreen()

    try:
        sys.exit(app.exec_())
    except:
        print("Exiting")


def except_hook(cls, exception, traceback):
    sys.__excepthook__(cls, exception, traceback)


if __name__ == '__main__':
    sys.excepthook = except_hook
    run_gui()