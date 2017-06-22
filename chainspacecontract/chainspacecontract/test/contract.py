from multiprocessing import Process
import time
import unittest

import requests

from chainspacecontract.contract import ChainspaceContract
from chainspacecontract.examples.increment import contract as increment_contract
from chainspacecontract.examples.increment_with_custom_checker import contract as increment_with_custom_checker_contract
from chainspacecontract.examples.bank_unauthenticated import contract as bank_unauthenticated_contract
from chainspacecontract.examples.bank_authenticated import contract as bank_authenticated_contract
from chainspacecontract.examples import bank_authenticated


class TestIncrement(unittest.TestCase):
    """
    def test_increment_checker_service(self):
        checker_service_process = Process(target=increment_contract.run_checker_service)
        checker_service_process.start()
        time.sleep(0.1)

        response = requests.post('http://127.0.0.1:5000/increment', json={
            'inputs': [1],
            'outputs': [2],
            'parameters': {},
            'reference_inputs': [],
            'returns': {},
        })
        response_json = response.json()
        self.assertTrue(response_json['success'])

        response = requests.post('http://127.0.0.1:5000/increment', json={
            'inputs': [1],
            'outputs': [3],
            'parameters': {},
            'reference_inputs': [],
            'returns': {},
        })
        response_json = response.json()
        self.assertFalse(response_json['success'])

        checker_service_process.terminate()
        checker_service_process.join()

    def test_increment_with_custom_checker_service(self):
        checker_service_process = Process(target=increment_with_custom_checker_contract.run_checker_service)
        checker_service_process.start()
        time.sleep(0.1)

        response = requests.post('http://127.0.0.1:5000/increment', json={
            'inputs': [1],
            'outputs': [2],
            'parameters': {},
            'reference_inputs': [],
            'returns': {},
        })
        response_json = response.json()
        self.assertTrue(response_json['success'])

        response = requests.post('http://127.0.0.1:5000/increment', json={
            'inputs': [1],
            'outputs': [3],
            'parameters': {},
            'reference_inputs': [],
            'returns': {},
        })
        response_json = response.json()
        self.assertFalse(response_json['success'])

        checker_service_process.terminate()
        checker_service_process.join()

    def test_bank_unauthenticated_checker_service(self):
        checker_service_process = Process(target=bank_unauthenticated_contract.run_checker_service)
        checker_service_process.start()
        time.sleep(0.1)

        response = requests.post('http://127.0.0.1:5000/transfer', json={
            'inputs': [{'name': 'alice', 'balance': 10}, {'name': 'bob', 'balance': 10}],
            'outputs': [{'name': 'alice', 'balance': 5}, {'name': 'bob', 'balance': 15}],
            'parameters': {'amount': 5},
            'reference_inputs': [],
            'returns': {},
        })
        response_json = response.json()
        self.assertTrue(response_json['success'])

        response = requests.post('http://127.0.0.1:5000/transfer', json={
            'inputs': [{'name': 'alice', 'balance': 10}, {'name': 'bob', 'balance': 10}],
            'outputs': [{'name': 'alice', 'balance': 5}, {'name': 'bob', 'balance': 16}],
            'parameters': {'amount': 5},
            'reference_inputs': [],
            'returns': {},
        })
        response_json = response.json()
        self.assertFalse(response_json['success'])

        checker_service_process.terminate()
        checker_service_process.join()
    """



    #############################################################################################
    # test an authenticated bank transfer
    #############################################################################################
    def test_bank_authenticated_checker_service(self):
        checker_service_process = Process(target=bank_authenticated_contract.run_checker_service)
        checker_service_process.start()
        time.sleep(0.1)

        # NOTE: export public key
        """
        G = EcGroup()
        g = G.generator()
        priv = G.order().random()
        pub = priv * g
        byte_string = pub.export()
        print hexlify(byte_string)
        print EcPt.from_binary(byte_string, G) == pub
        """

        response = requests.post('http://127.0.0.1:5000/auth_transfer', 
            json=bank_authenticated.auth_transfer(
                [{'name': 'alice', 'balance': 10}, {'name': 'bob', 'balance': 10}],
                None,
                {'amount': 3},
                '83C72CF7E1BA9F120C5A45135A0FE3DA59D7771BB9C670B63134A8B0'
            )
        )
        response_json = response.json()
        self.assertTrue(response_json['success'])

        checker_service_process.terminate()
        checker_service_process.join()





if __name__ == '__main__':
    unittest.main()